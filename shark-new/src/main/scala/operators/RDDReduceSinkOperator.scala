package shark.operators

import shark.ReduceKey
import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.plan.TableDesc
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.Deserializer
import org.apache.hadoop.hive.serde2.SerDe
import org.apache.hadoop.io.BytesWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf

import java.io.ObjectOutputStream
import java.io.ByteArrayOutputStream
import java.util.ArrayList

import scala.collection.JavaConversions._


class RDDReduceSinkOperator extends ReduceSinkOperator with RDDOperator {

  var joinTag = 0

  var keyTableDesc: TableDesc = null

  var valueTableDesc: TableDesc = null

  var keySer: SerDe = null

  var valueSer: SerDe = null

  var kIsText: Boolean = false

  var keyObjInspector: ObjectInspector = null

  var valObjInspector: ObjectInspector = null

  override def initializeOp(hconf: Configuration) { 
    keyEval = conf.getKeyCols.map(ExprNodeEvaluatorFactory.get(_)).toArray
    numDistributionKeys = conf.getNumDistributionKeys()
    val distinctColIndices = conf.getDistinctColumnIndices()
    numDistinctExprs = distinctColIndices.size()
    valueEval = conf.getValueCols.map(ExprNodeEvaluatorFactory.get(_)).toArray
    val rowInspector = inputObjInspectors(0)

    joinTag = conf.getTag();
    LOG.info("Using tag = " + joinTag)

    keyTableDesc = conf.getKeySerializeInfo()
    keySer = keyTableDesc.getDeserializerClass()
          .newInstance().asInstanceOf[SerDe]
    keySer.initialize(null, keyTableDesc.getProperties())
    kIsText = keySer.getSerializedClass().equals(classOf[Text])

    valueTableDesc = conf.getValueSerializeInfo()
    valueSer = valueTableDesc.getDeserializerClass()
          .newInstance().asInstanceOf[SerDe]
    valueSer.initialize(null, valueTableDesc.getProperties())
    
    /*
     * Doesn't handle group by distinct
     * //    val keyFieldInspectors = keyEval.map(eval => eval.initialize(rowInspector)).toList

    keyObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      conf.getOutputKeyColumnNames(), keyFieldInspectors)
    valObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      conf.getOutputValueColumnNames(), valFieldInspectors) */
    keyObjInspector = ReduceSinkWrapper.initEvaluatorsAndReturnStruct(
      keyEval,
      distinctColIndices,
      conf.getOutputKeyColumnNames,
      numDistributionKeys,
      rowInspector)
    val valFieldInspectors = valueEval.map(eval => eval.initialize(rowInspector)).toList
    valObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      conf.getOutputValueColumnNames(), valFieldInspectors) 
    val ois = new ArrayList[ObjectInspector]
    ois.add(keySer.getObjectInspector)
    ois.add(valueSer.getObjectInspector)
    outputObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      List("KEY","VALUE"), ois)
    initializeChildren(hconf)
  }

  def getKeyEval = keyEval

  def getValueEval = valueEval
  
  def deserializeKey(bytes: BytesWritable) = {    
    keySer.asInstanceOf[Deserializer].deserialize(bytes)
  }

  def deserializeValue(bytes: BytesWritable) = {
    valueSer.asInstanceOf[Deserializer].deserialize(bytes)
  }

  override def processIter[T](iter: Iterator[T]) = {
      var rowInspector = getInputObjInspectors()(0)    
      iter.map { row => {
        val key = keySer.serialize(getKeyEval.map { k => k.evaluate(row) },
          keyObjInspector).asInstanceOf[BytesWritable]
        val value = valueSer.serialize(getValueEval.map { v => v.evaluate(row) },
          valObjInspector).asInstanceOf[BytesWritable]
        val keyArr = new ReduceKey(new Array[Byte](key.getLength))
        val valueArr = new Array[Byte](value.getLength)
        Array.copy(key.getBytes, 0, keyArr.bytes, 0, key.getLength)
        Array.copy(value.getBytes, 0, valueArr, 0, value.getLength)
        (keyArr, valueArr)
      }}
  }
}

