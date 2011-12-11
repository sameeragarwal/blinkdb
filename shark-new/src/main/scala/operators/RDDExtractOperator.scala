package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import shark.ReduceKey
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.exec.ExtractOperator
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator
import org.apache.hadoop.hive.ql.plan.ReduceSinkDesc
import org.apache.hadoop.hive.ql.plan.TableDesc
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.Deserializer
import org.apache.hadoop.io.BytesWritable

import scala.collection.JavaConversions._
import scala.reflect.BeanProperty
import java.util.ArrayList

class RDDExtractOperator extends ExtractOperator with RDDOperator {

  def getEval = eval

  @BeanProperty
  var reduceSinkConf: ReduceSinkDesc = null

  override def initializeOp(hconf:Configuration) {
    eval = ExprNodeEvaluatorFactory.get(conf.getCol)
    outputObjInspector = eval.initialize(inputObjInspectors(0))
    initializeChildren(hconf)
  }

  // TODO: Remove duplicated deserialization code
  def initSerializer(tableDesc: TableDesc): Deserializer = {
    val deserializer = tableDesc.getDeserializerClass.
      newInstance().asInstanceOf[Deserializer]
    deserializer.initialize(null, tableDesc.getProperties())
    deserializer
  }

  def deserialize(serDe: Deserializer, bytes: BytesWritable) = {    
    serDe.deserialize(bytes)
  }

  def deserializeStandardObject(serDe: Deserializer, bytes: BytesWritable) = {
    val deserializedObj = deserialize(serDe, bytes)
    if (deserializedObj == null)
      Array()
    else
      ObjectInspectorUtils.copyToStandardObject(deserializedObj,
                                                serDe.getObjectInspector.asInstanceOf[StructObjectInspector])            .asInstanceOf[ArrayList[Object]].toArray
  }

  override def processIter[T](iter: Iterator[T]) = {
    val rowInspector = getInputObjInspectors()(0).asInstanceOf[StructObjectInspector]
    
    val deserializer = initSerializer(reduceSinkConf.getValueSerializeInfo)
    val bytes = new BytesWritable()
    iter.map { row => {
      row match {
        case (key: ReduceKey,
              value: Array[Byte]) => { 
          bytes.set(value)
          deserialize(deserializer, bytes)
        }
        case _ => throw new Exception
      }
    }}
  }

  def processOrderedRDD[K <% Ordered[K]: ClassManifest, V: ClassManifest, T](rdd: RDD[T]): RDD[_] = {
     val newRDD = rdd match {
      case r:RDD[(K,V)] => {
        // TODO: use sortByKey() instead
        val rangeRDD = r.partitionBy(new RangePartitioner(r.splits.size, r, true))
        new SortedRDD(rangeRDD, true)
      }
      case _ => rdd
    }
    newRDD
  }
 
  override def processRDD[T](rdd: RDD[T]): RDD[_] = {
    val hasOrder = getParentOperators()(0) match {
      case op:RDDReduceSinkOperator 
              => (op.getConf.getOrder != null)
      case _ => false
    }
    super.processRDD(if (hasOrder) processOrderedRDD(rdd) else rdd)
  }
}

