package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import shark.ReduceKey
import shark.KeyWrapperFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.exec.GroupByOperator
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator
import org.apache.hadoop.hive.ql.exec.Utilities

import org.apache.hadoop.hive.ql.plan.AggregationDesc
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc
import org.apache.hadoop.hive.ql.plan.GroupByDesc
import org.apache.hadoop.hive.ql.plan.ReduceSinkDesc
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption
import org.apache.hadoop.hive.serde2.Deserializer
import org.apache.hadoop.hive.serde2.SerDe
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils
import org.apache.hadoop.io.BytesWritable
import org.apache.hadoop.io.Text

import java.util.ArrayList

import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import scala.reflect.BeanProperty

class RDDGroupByOperator extends GroupByOperator with RDDOperator with Logging {

  @transient var keyFactory: KeyWrapperFactory = null

  @transient var rowInspector: ObjectInspector = null
  
  @transient var aggregationEvals: Array[GenericUDAFEvaluator] = null

  @transient var keyObjectInspector: StructObjectInspector = null

  @transient var keySer: Deserializer = null
  
  @transient var valueSer: Deserializer = null

  @transient var unionExprEvaluator: ExprNodeEvaluator = null
  
  @BeanProperty
  var reduceSinkConf: ReduceSinkDesc = null

  def getAggregationEvals: Array[GenericUDAFEvaluator] = aggregationEvals

  def getKeyFields: Array[ExprNodeEvaluator] = keyFields

  def getOutputObjInspector: ObjectInspector = outputObjInspector

  def getKeyObjectInspector: ObjectInspector = keyObjectInspector

  def deserializeKey(bytes: BytesWritable) = {    
    keySer.asInstanceOf[Deserializer].deserialize(bytes)
  }
  
  def deserializeValue(bytes: BytesWritable) = {
    valueSer.asInstanceOf[Deserializer].deserialize(bytes)
  }


  // Basically copied from Hive code, sorry for lack of clarity
  override def initializeOp(hconf: Configuration){ 
    aggregationEvals = conf.getAggregators.map { agg => 
      (agg.getGenericUDAFEvaluator.getClass).newInstance.asInstanceOf[GenericUDAFEvaluator]
    }.toArray
    rowInspector = inputObjInspectors(0).asInstanceOf[StructObjectInspector]
    keyFields = new Array[ExprNodeEvaluator](conf.getKeys().size())
    keyObjectInspectors = new Array[ObjectInspector](conf.getKeys().size())
    currentKeyObjectInspectors = new Array [ObjectInspector](conf.getKeys().size());
    keyFields = conf.getKeys().map(k => ExprNodeEvaluatorFactory.get(k)).toArray
    keyObjectInspectors = keyFields.map(k => k.initialize(rowInspector))
    currentKeyObjectInspectors = keyObjectInspectors.map { k => 
      ObjectInspectorUtils.getStandardObjectInspector(k, ObjectInspectorCopyOption.WRITABLE)
    }

    aggregationParameterFields = conf.getAggregators.toArray.map { aggr => 
      aggr.asInstanceOf[AggregationDesc].getParameters.toArray.map { param =>
        ExprNodeEvaluatorFactory.get(param.asInstanceOf[ExprNodeDesc])
      }
    }
    aggregationParameterObjectInspectors = aggregationParameterFields.map { aggr =>
      aggr.map { param => param.initialize(rowInspector) }
    }
    
    val aggObjInspectors = aggregationEvals.zipWithIndex.map { pair =>
      pair._1.init(conf.getAggregators.get(pair._2).getMode,
        aggregationParameterObjectInspectors(pair._2))
    }

    val fieldNames = conf.getOutputColumnNames    
    val keyFieldNames = fieldNames.slice(0,keyFields.length)
    
    val totalFields = keyFields.length + aggregationEvals.length
    objectInspectors = new ArrayList[ObjectInspector](totalFields)
    val keyois = new ArrayList[ObjectInspector](totalFields)
    keyObjectInspectors.foreach(keyois.add(_))
    keyObjectInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      keyFieldNames, keyois)
    currentKeyObjectInspectors.foreach { objectInspectors.add(_) }

    // Initialize unionExpr. KEY has union field as the last field if there are distinct aggrs.
    val sfs = rowInspector.asInstanceOf[StructObjectInspector].getAllStructFieldRefs
    if (sfs.size > 0) {
      val keyField = sfs.get(0)
      if (keyField.getFieldName.toUpperCase.equals(Utilities.ReduceField.KEY.name)) {
        val keyObjInspector = keyField.getFieldObjectInspector
        if (keyObjInspector.isInstanceOf[StandardStructObjectInspector]) {
          val keysfs = rowInspector.asInstanceOf[StructObjectInspector].getAllStructFieldRefs
          if (keysfs.size() > 0) {
            val sf = keysfs.get(keysfs.size() - 1)
            if (sf.getFieldObjectInspector().getCategory().equals(
                ObjectInspector.Category.UNION)) {
              unionExprEvaluator = ExprNodeEvaluatorFactory.get(
                new ExprNodeColumnDesc(
                  TypeInfoUtils.getTypeInfoFromObjectInspector(
                    sf.getFieldObjectInspector),
                  keyField.getFieldName + "." + sf.getFieldName, null, false))
              unionExprEvaluator.initialize(rowInspector)
            }
          }
        }
      }
    }

    aggObjInspectors.foreach { objectInspectors.add(_) }
    outputObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      fieldNames, objectInspectors)
    
    keyFactory = new KeyWrapperFactory(keyFields, keyObjectInspectors, currentKeyObjectInspectors)
    reduceSinkConf match {
      case conf: ReduceSinkDesc => {
        val keyTableDesc = conf.getKeySerializeInfo
        keySer = keyTableDesc.getDeserializerClass.
          newInstance().asInstanceOf[Deserializer]
        keySer.initialize(null, keyTableDesc.getProperties())
        val valueTableDesc = conf.getValueSerializeInfo
        valueSer = valueTableDesc.getDeserializerClass
          .newInstance().asInstanceOf[SerDe]
        valueSer.initialize(null, valueTableDesc.getProperties())
      }
      case null =>
        Unit
    }

    initializeChildren(hconf)
  }

  override def newAggregations(): Array[AggregationBuffer] = {
    aggregationEvals.map(eval => eval.getNewAggregationBuffer)
  }

  def getAggregationParameterFields() = aggregationParameterFields
  
  override def processRDD[T](rdd: RDD[T]): RDD[_] = {
    if (conf.getMode == GroupByDesc.Mode.FINAL || conf.getMode == GroupByDesc.Mode.MERGEPARTIAL|| conf.getMode == GroupByDesc.Mode.COMPLETE)
      super.processRDD(rdd.asInstanceOf[RDD[(Any,Any)]].groupByKey())
    else
      super.processRDD(rdd)
  }

  override def processIter[T](iter: Iterator[T]) = {
    if (conf.getMode == GroupByDesc.Mode.FINAL || conf.getMode == GroupByDesc.Mode.MERGEPARTIAL || conf.getMode == GroupByDesc.Mode.COMPLETE)  
      postShuffleGroupBy(iter)
    else 
      preShuffleGroupBy(iter)
  }

  def postShuffleGroupBy[T](iter: Iterator[T]) = {
    val bytes = new BytesWritable()
    logInfo("Running Post Shuffle Group-By")
    val outputCache = new Array[Object](keyFields.length + aggregationEvals.length)
    val keys = keyFactory.getKeyWrapper()
    iter.map(pair => {
      pair match {
        case (key: ReduceKey,
              values: Seq[Array[Byte]]) => {
          bytes.set(key.bytes)
          val deserializedKey = deserializeKey(bytes)
        /*  val writableKey = ObjectInspectorUtils.copyToStandardObject(
            deserializedKey, getKeyObjectInspector,ObjectInspectorCopyOption.WRITABLE)*/
          val aggrs = newAggregations()
          values.asInstanceOf[Seq[Array[Byte]]].foreach(v => {
            bytes.set(v)
            val deserializedValue = deserializeValue(bytes)
            keys match {
              case k: KeyWrapperFactory#ListKeyWrapper => 
                k.getNewKey(Array(deserializedKey,deserializedValue), rowInspector)
              case k: KeyWrapperFactory#TextKeyWrapper => 
                k.getNewKey(Array(deserializedKey,deserializedValue), rowInspector)
            }
            aggregate(Array(deserializedKey, deserializedValue), aggrs)
          })
          val arr = keys match {
            case k: KeyWrapperFactory#ListKeyWrapper => 
              k.getKeyArray
            case k: KeyWrapperFactory#TextKeyWrapper => 
              k.getKeyArray
          }
          arr.zipWithIndex foreach { case(key, i) => outputCache(i) = key }
          aggrs.zipWithIndex.foreach { case(aggr, i) => 
            outputCache(i + arr.length) = aggregationEvals(i).evaluate(aggr)
          }
          
          outputCache
          // Not sure if i need these
//          val keys = writableKey.asInstanceOf[ArrayList[Any]].toArray
//          ObjectInspectorUtils.copyToStandardObject(keys ++ vals,
//                                                    getOutputObjInspector, ObjectInspectorCopyOption.WRITABLE)
          
        }
      }
    })
  }

  def aggregate(row: AnyRef,
                aggregations: Array[AggregationBuffer]) {
    aggregations.zipWithIndex foreach { case(aggr, i) => {
      aggregationEvals(i).aggregate(aggr,
                                    aggregationParameterFields(i).map( _.evaluate(row)))
    }}
  }
/*  def aggregate(row: Array[Any], 
                hashAggregations: HashMap[Any, Array[AggregationBuffer]]) {
    val aggs = hashAggregations.get(key) match {
      case Some(aggs) => aggs
      case None => {
        val aggs = newAggregations
        hashAggregations.put(key, aggs)
        aggs
      }
    }
    Range(0,aggs.size).foreach(i => {
      val o = aggregationParameterFields(i).map( _.evaluate(row))
      aggregationEvals(i).aggregate(aggs(i),o)
    })
  }*/

  def preShuffleGroupBy[T](iter: Iterator[T]) = {
    logInfo("Pre-Shuffle Group-By")
    val hashAggregations = new java.util.HashMap[KeyWrapperFactory#KeyWrapper, Array[AggregationBuffer]]()
    iter.foreach { case row:AnyRef => {
      
      //val key = new ArrayList[Any]()
      val newKeys = keyFactory.getKeyWrapper()
      newKeys match {
        case k: KeyWrapperFactory#ListKeyWrapper => {
          k.getNewKey(row, rowInspector)
          k.setHashKey()
        }
        case k: KeyWrapperFactory#TextKeyWrapper => {
          k.getNewKey(row, rowInspector)
          k.setHashKey()
        }
      }
      var aggs = hashAggregations.get(newKeys)
      if (aggs == null) {
        val newKeyProber = newKeys match {
          case k: KeyWrapperFactory#ListKeyWrapper => {
            k.copyKey()
          }
          case k: KeyWrapperFactory#TextKeyWrapper => {
            k.copyKey()
          }
        }
        aggs = newAggregations()
        hashAggregations.put(newKeyProber, aggs)
      }
      aggregate(row, aggs)
      Unit
    }}
    val outputCache = new Array[Object](keyFields.length + aggregationEvals.length)
    hashAggregations.toIterator.map { case(key, aggrs) => {
      val arr = key match {
        case k: KeyWrapperFactory#ListKeyWrapper => 
          k.getKeyArray
        case k: KeyWrapperFactory#TextKeyWrapper => 
          k.getKeyArray
      }
      arr.zipWithIndex foreach { case(key, i) => outputCache(i) = key }
      aggrs.zipWithIndex foreach { case(aggr, i) => outputCache(i + arr.length) = aggregationEvals(i).evaluate(aggr) }
      outputCache
      /*      // Need to copy values to a different object since the result of evaluate is shared
       val vals = kvPair._2.zipWithIndex.map(pair =>
       aggregationEvals(pair._2).evaluate(pair._1))
       ObjectInspectorUtils.
       copyToStandardObject(
       keys ++ vals,
       getOutputObjInspector,
       ObjectInspectorCopyOption.WRITABLE)*/
    }}
  }
}


