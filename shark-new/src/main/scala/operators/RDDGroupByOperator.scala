package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.exec.GroupByOperator
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator
import org.apache.hadoop.hive.ql.plan.AggregationDesc
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc
import org.apache.hadoop.hive.ql.plan.GroupByDesc
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.io.BytesWritable

import java.util.ArrayList

import scala.collection.JavaConversions._


class RDDGroupByOperator extends GroupByOperator {

  @transient val tagToRDD = scala.collection.mutable.Map[Int,RDD[(Any,Any)]]()

  @transient var aggregationEvals: Array[GenericUDAFEvaluator] = null

  @transient var keyObjectInspector: StructObjectInspector = null

  def addInputRDD(tag: Int,rdd: RDD[(Any,Any)]) = { tagToRDD(tag) = rdd }

  def getAggregationEvals: Array[GenericUDAFEvaluator] = aggregationEvals

  def getKeyFields: Array[ExprNodeEvaluator] = keyFields

  def getOutputObjInspector: ObjectInspector = outputObjInspector

  def getKeyObjectInspector: ObjectInspector = keyObjectInspector

  override def initializeOp(hconf: Configuration){ 
    aggregationEvals = conf.getAggregators.map { agg => 
      (agg.getGenericUDAFEvaluator.getClass).newInstance.asInstanceOf[GenericUDAFEvaluator]
    }.toArray
    val rowInspector = inputObjInspectors(0).asInstanceOf[StructObjectInspector]
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
    aggObjInspectors.foreach { objectInspectors.add(_) }
    outputObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      fieldNames, objectInspectors)
    initializeChildren(hconf)
  }

  override def newAggregations(): Array[AggregationBuffer] = {
    aggregationEvals.map(eval => eval.getNewAggregationBuffer)
  }

  def getAggregationParameterFields() = aggregationParameterFields

  override def processOp(rdd: Object, tag: Int) {    
    val newRDD = OperatorTreeCache.get(this) match {
      case Some(r) => { 
        println("Found group by op in cache")
        r
      }
      case None => {
        println("Didn't find group by op in cache")
        val r = processRDD(rdd.asInstanceOf[RDD[(Any,Any)]])
        r.cache()
        OperatorTreeCache.put(this,r)
        r
      }
    }
    forward(newRDD, inputObjInspectors(0))
  }
  
  def processRDD(rdd: RDD[(Any,Any)]): RDD[Any] = {
    var newRDD = rdd.asInstanceOf[RDD[Any]]
    if (getParentOperators()(0).isInstanceOf[ReduceSinkOperator])
      newRDD = rdd.groupByKey.asInstanceOf[RDD[Any]]
    val table_desc = RDDOperator.tableDesc   
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId

    // TODO(rxin): Break this down ... way too long.
    newRDD = newRDD.mapPartitions((iter) => {
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      val jc = new JobConf()
      RDDOperator.getTopOperators(top_op).foreach(_.initialize(jc,oi))
      val current_op = top_op.findOperator(current_id).asInstanceOf[RDDGroupByOperator]
      val rowInspector = current_op.getInputObjInspectors()(0).asInstanceOf[StructObjectInspector]
      val aggregationParameterFields = current_op.getAggregationParameterFields
      val aggregationEvals = current_op.getAggregationEvals
      val hashAggregations = new scala.collection.mutable.HashMap[Any, Array[AggregationBuffer]]
      val keyFields = current_op.getKeyFields
      def aggregate(row: Array[Any],key: Any): Unit = {
        val aggs = hashAggregations.get(key) match {
          case Some(aggs) => aggs
          case None => {
            val aggs = current_op.newAggregations
            hashAggregations.put(key, aggs)
            aggs
          }
        }
        Range(0,aggs.size).foreach(i => {
          val o = aggregationParameterFields(i).map( _.evaluate(row))
          aggregationEvals(i).aggregate(aggs(i),o)
        })
      }
      
      def deserializeKey(key: BytesWritable) = {
        val parent = current_op.getParentOperators()(0)
        parent match {
          case op: RDDReduceSinkOperator =>
            op.deserializeKey(key)

        }
      }
      def deserializeValue(value: BytesWritable): Any = {
        val parent = current_op.getParentOperators()(0)
        parent match {
          case op: RDDReduceSinkOperator => {
            if (op.deserializeValue(value) != null){
              op.deserializeValue(value)
            }
            else
              null
          }
        }
      }
      val conf = current_op.getConf
      if (conf.getMode == GroupByDesc.Mode.FINAL || conf.getMode == GroupByDesc.Mode.MERGEPARTIAL) {
        iter.map(pair => {
          pair match {
          //After ReduceSink
            case (key: SerializableWritable[BytesWritable],
                  values: Seq[SerializableWritable[BytesWritable]]) => {
              val deserializedKey = deserializeKey(key.value)
              val writableKey = ObjectInspectorUtils.copyToStandardObject(
                deserializedKey,current_op.getKeyObjectInspector,ObjectInspectorCopyOption.WRITABLE)
              values.asInstanceOf[Seq[SerializableWritable[BytesWritable]]].foreach(v => {
                val deserializedValue = deserializeValue(v.value)
                aggregate(Array(deserializedKey, deserializedValue),writableKey)
              })
              
              val vals = hashAggregations.get(writableKey) match {
                case Some(arr) => arr.zipWithIndex.map({
                  case(aggBuf,index) =>
                    aggregationEvals(index).evaluate(aggBuf)})
              }
              val keys = writableKey.asInstanceOf[ArrayList[Any]].toArray
              ObjectInspectorUtils.copyToStandardObject(keys ++ vals,
                current_op.getOutputObjInspector, ObjectInspectorCopyOption.WRITABLE)
            }
          }
        })}
      else {
      while(iter.hasNext) {
        iter.next.asInstanceOf[AnyRef] match {
          //Before ReduceSink (Partial Aggregation)
          case kvPair: AnyRef => { 
            val key = new ArrayList[Any]()
            keyFields.foreach(field => key.add(field.evaluate(kvPair)))
            val writableKey = ObjectInspectorUtils.copyToStandardObject(key, 
              current_op.getKeyObjectInspector,ObjectInspectorCopyOption.WRITABLE)
            aggregate(kvPair.asInstanceOf[Array[Any]],writableKey)
          }
        }
      }
      hashAggregations.map(kvPair => {
        val keys = kvPair._1.asInstanceOf[ArrayList[Any]].toArray
        // Need to copy values to a different object since the result of evaluate is shared
        val vals = kvPair._2.zipWithIndex.map(pair =>
            aggregationEvals(pair._2).evaluate(pair._1))
        ObjectInspectorUtils.copyToStandardObject(keys ++ vals, current_op.getOutputObjInspector,
          ObjectInspectorCopyOption.WRITABLE)
      }).toIterator
      }
    })
    newRDD
  }
}

