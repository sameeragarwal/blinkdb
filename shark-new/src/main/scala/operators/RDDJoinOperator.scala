package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import shark.ReduceKey
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator
import org.apache.hadoop.hive.ql.exec.JoinOperator
import org.apache.hadoop.hive.ql.exec.JoinUtil
import org.apache.hadoop.hive.ql.plan.JoinDesc
import org.apache.hadoop.hive.ql.plan.TableDesc
import org.apache.hadoop.hive.ql.plan.ReduceSinkDesc
import org.apache.hadoop.hive.serde2.Deserializer
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.io.BytesWritable

import java.util.ArrayList
import java.util.HashMap

import scala.reflect.BeanProperty
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


class RDDJoinOperator extends JoinOperator with RDDOperator {

  // Only works for 2 parents right now
  @BeanProperty 
  var reduceSinkConfs = new Array[ReduceSinkDesc](2)

  @transient val tagToValueSer = new HashMap[Int, Deserializer]()

  @transient val joinVals = new HashMap[java.lang.Byte, java.util.List[ExprNodeEvaluator]]();
  
  def getJoinVal(i: Int) = {
    joinVals
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

  // Need to store multiple OIs to initialize properly
  override def initializeOp(hconf: Configuration) {
     val NOTSKIPBIGTABLE = -1
    order = conf.getTagOrder
    condn = conf.getConds
    noOuterJoin = conf.isNoOuterJoin
    JoinUtil.populateJoinKeyValue(joinVals, conf.getExprs(), order, NOTSKIPBIGTABLE)
    val joinValuesObjectInspectors = JoinUtil.getObjectInspectorsFromEvaluators(joinVals,
        inputObjInspectors, NOTSKIPBIGTABLE)
    val joinValuesStandardObjectInspectors = JoinUtil.getStandardObjectInspectors(
        joinValuesObjectInspectors, NOTSKIPBIGTABLE)
//    outputObjInspector = RDDJoinOperator.getJoinOutputObjectInspector(order,
//      joinValuesStandardObjectInspectors, conf);
    outputObjInspector = RDDJoinOperator.getJoinOutputObjectInspector(order,
      joinValuesObjectInspectors, conf);
    
    initializeChildren(hconf)
  }
 
  def initSerializer(tableDesc: TableDesc): Deserializer = {
    val deserializer = tableDesc.getDeserializerClass.
      newInstance().asInstanceOf[Deserializer]
    deserializer.initialize(null, tableDesc.getProperties())
    deserializer
  }

  def initSerializers() {
    reduceSinkConfs.zipWithIndex.foreach { case (reduceSinkConf, tag) => {
      tagToValueSer.put(tag, initSerializer(reduceSinkConf.getValueSerializeInfo))
    }}
  }

  def getOrder(index: Int) = {
    order(index).byteValue.toInt
  }

  def getJoinCondition() = condn



  def processRDDs(rdds: HashMap[Int, RDD[_]]): RDD[_] = {
    val joinType = condn(0).getType()
    val left = rdds.get(getOrder(0)).asInstanceOf[RDD[
      (ReduceKey, Array[Byte])]]
    val right = rdds.get(getOrder(1)).asInstanceOf[RDD[
      (ReduceKey, Array[Byte])]]

    val joinedRDD = joinType match {
      case JoinDesc.INNER_JOIN       => left.join(right)
      case JoinDesc.LEFT_OUTER_JOIN  => left.leftOuterJoin(right)
      case JoinDesc.RIGHT_OUTER_JOIN => left.rightOuterJoin(right)
      case JoinDesc.FULL_OUTER_JOIN  => left.fullOuterJoin(right)
      case JoinDesc.LEFT_SEMI_JOIN   => left.join(right) // Not implemented
      case JoinDesc.UNIQUE_JOIN      => left.join(right) // Not implemented
    }
    processRDD(joinedRDD)
  }

  override def processIter[T](iter: Iterator[T]): Iterator[_] = {
    initSerializers()
    val bytes = new BytesWritable()
    val row = new Array[Object](joinVals(0.toByte).size() + joinVals(1.toByte).size())
    val tmp = new Array[Object](2)
    iter.map {
      case (key, values) =>
        values match {
          case (left, right) => {
            left match {
              case Some(x: Array[Byte]) => {
                bytes.set(x)
                tmp(1) = deserialize(tagToValueSer.get(0), bytes)
                joinVals(0.toByte).zipWithIndex.foreach { case(eval, i) => row(i) = eval.evaluate(tmp) }
              }
              case x: Array[Byte] =>  {
                bytes.set(x)
                tmp(1) = deserialize(tagToValueSer.get(0), bytes)
                joinVals(0.toByte).zipWithIndex.foreach { case(eval, i) => row(i) = eval.evaluate(tmp) }
              }
              case None => Array(null)
            }
            right match {
              case Some(x: Array[Byte]) => {
                bytes.set(x)
                tmp(1) = deserialize(tagToValueSer.get(1), bytes)
                joinVals(1.toByte).zipWithIndex.foreach { case(eval, i) => row(i + joinVals(0.toByte).size) = eval.evaluate(tmp) }
              }
              case x: Array[Byte] => {
                bytes.set(x)
                tmp(1) = deserialize(tagToValueSer.get(1), bytes)
                joinVals(1.toByte).zipWithIndex.foreach { case(eval, i) => row(i + joinVals(0.toByte).size) = eval.evaluate(tmp) }
              }
              case None => Array(null)
            }
            row
          }
        }
    }
  }
  /*override def processIter[T](iter: Iterator[T]): Iterator[_] = {
    initSerializers()
    val bytes = new BytesWritable()

    iter.map {
      case (key, values) =>
        values match {
          case (left, right) => {
            val a = left match {
              case Some(x: Array[Byte]) => {
                bytes.set(x)
                deserializeStandardObject(tagToValueSer.get(0), bytes)
              }
              case x: Array[Byte] =>  {
                bytes.set(x)
                deserializeStandardObject(tagToValueSer.get(0), bytes)
              }
              case None => Array(null)
            }
            val b = right match {
              case Some(x: Array[Byte]) => {
                bytes.set(x)
                deserializeStandardObject(tagToValueSer.get(1), bytes)
              }
              case x: Array[Byte] => {
                bytes.set(x)
                deserializeStandardObject(tagToValueSer.get(1), bytes)
              }
              case None => Array(null)
            }
            a ++ b
          }
        }
    }
  }*/
}
