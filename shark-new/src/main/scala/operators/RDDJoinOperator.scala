package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator
import org.apache.hadoop.hive.ql.exec.JoinOperator
import org.apache.hadoop.hive.ql.exec.JoinUtil
import org.apache.hadoop.hive.ql.plan.JoinDesc
import org.apache.hadoop.hive.serde2.Deserializer
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.io.BytesWritable

import java.util.ArrayList
import java.util.HashMap

import scala.collection.JavaConversions._


object RDDJoinOperator {
  def getJoinOutputObjectInspector[T: ClassManifest](
    order: Array[java.lang.Byte],
    aliasToObjectInspectors: HashMap[java.lang.Byte, java.util.List[ObjectInspector]],
    conf: T) =
  {
    val structFieldObjectInspectors = new ArrayList[ObjectInspector]();
    order.foreach { alias => {
      val oiList: java.util.List[ObjectInspector] = aliasToObjectInspectors.get(alias);
      structFieldObjectInspectors.addAll(oiList);
    }}

    ObjectInspectorFactory.getStandardStructObjectInspector(
      conf.asInstanceOf[JoinDesc].getOutputColumnNames(), structFieldObjectInspectors);
  }
}


class RDDJoinOperator extends JoinOperator {

  @transient val tagToRDD = scala.collection.mutable.Map[Int,RDD[(Any,Any)]]()

  def addInputRDD(tag: Int,rdd: RDD[(Any,Any)]) = {tagToRDD(tag) = rdd}

  override def initializeOp(hconf: Configuration) {
    val NOTSKIPBIGTABLE = -1
    order = conf.getTagOrder
    condn = conf.getConds
    noOuterJoin = conf.isNoOuterJoin
    val joinValues = new HashMap[java.lang.Byte, java.util.List[ExprNodeEvaluator]]();
    JoinUtil.populateJoinKeyValue(joinValues, conf.getExprs(), order, NOTSKIPBIGTABLE)
    val joinValuesObjectInspectors = JoinUtil.getObjectInspectorsFromEvaluators(joinValues,
        inputObjInspectors, NOTSKIPBIGTABLE)
    val joinValuesStandardObjectInspectors = JoinUtil.getStandardObjectInspectors(
        joinValuesObjectInspectors, NOTSKIPBIGTABLE)
    outputObjInspector = RDDJoinOperator.getJoinOutputObjectInspector(order,
      joinValuesStandardObjectInspectors, conf);
    initializeChildren(hconf)
  }
 
  override def processOp(o: Object, tag: Int){
    processOp(o.asInstanceOf[RDD[(Any,Any)]])
  }

  def getOrder(index: Int) = {
    order(index).byteValue.toInt
  }

  def getJoinCondition() = condn

  /**
   * Deserialize a value by looking up the parent ReduceSink Operator
   * corresponding to the index and using its deserializer.
   */
  def deserializeValue(value: SerializableWritable[BytesWritable], index: Int): Array[Object] = {
    val parents = getParentOperators
    val tag = getOrder(index)
    val requestedParent = parents.find {
      case parent: RDDReduceSinkOperator => parent.joinTag == tag
    }

    requestedParent match {
      case Some(parent: RDDReduceSinkOperator) => 
        parent.deserializeValue(value.value).asInstanceOf[Array[Object]]
    }
  }

  def processOp(rdd: RDD[(Any,Any)]) {
    val condition = condn
    val joinType = condition(0).getType()
    val left = tagToRDD(getOrder(0)).asInstanceOf[RDD[
      (SerializableWritable[BytesWritable],SerializableWritable[BytesWritable])]]
    val right = tagToRDD(getOrder(1)).asInstanceOf[RDD[
      (SerializableWritable[BytesWritable],SerializableWritable[BytesWritable])]]
    val tagToTableDescs = getParentOperators.map {
      case parent: RDDReduceSinkOperator => (parent.joinTag, parent.valueTableDesc)
     }.toMap
      
    val joinedRDD = joinType match {
      case JoinDesc.INNER_JOIN       => left.join(right)
      case JoinDesc.LEFT_OUTER_JOIN  => left.leftOuterJoin(right)
      case JoinDesc.RIGHT_OUTER_JOIN => left.rightOuterJoin(left)
      case JoinDesc.FULL_OUTER_JOIN  => left.fullOuterJoin(left)
      case JoinDesc.LEFT_SEMI_JOIN   => left.join(right)
      //case JoinDesc.UNIQUE_JOIN      =>
    }

    val newRDD = joinedRDD.mapPartitions { iter => {
        val tagToDeserializers = tagToTableDescs.mapValues { tableDesc => {
          val valueSer = tableDesc.getDeserializerClass()
          .newInstance().asInstanceOf[Deserializer]
          valueSer.initialize(null, tableDesc.getProperties())
          valueSer
        }}
        
        def deserializeValue(value: BytesWritable,tag: Int): Array[Object] = {
          if(tagToDeserializers(tag).deserialize(value) == null)
            return Array()
          ObjectInspectorUtils.copyToStandardObject(
            tagToDeserializers(tag).deserialize(value),tagToDeserializers(tag).getObjectInspector)
            .asInstanceOf[ArrayList[Object]].toArray
        }

        iter.map { case (key, values) =>
          values match {
            case (left, right) => {
              val a = left match {
                case Some(x: SerializableWritable[BytesWritable]) => deserializeValue(x.value,0)
                case x: SerializableWritable[BytesWritable] => deserializeValue(x.value,0)
                //case None => Array(NullWritable.get)
                case None => Array(null)
              }
              val b = right match {
                case Some(x: SerializableWritable[BytesWritable]) => deserializeValue(x.value,1)
                case x: SerializableWritable[BytesWritable] => deserializeValue(x.value,1)
                //case None => Array(NullWritable.get)
                case None => Array(null)
              }
              a ++ b
            }
          }
        }
      }
    }
    forward(newRDD,inputObjInspectors(0))
  }
}

