package shark.operators

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

import java.util.ArrayList

import scala.collection.JavaConversions._


class RDDReduceSinkOperator extends ReduceSinkOperator{

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
    val keyFieldInspectors = keyEval.map(eval => eval.initialize(rowInspector)).toList

    val valFieldInspectors = valueEval.map(eval => eval.initialize(rowInspector)).toList

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

    keyObjInspector = ObjectInspectorFactory.getStandardStructObjectInspector(
      conf.getOutputKeyColumnNames(), keyFieldInspectors)
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

  override def processOp(o: Object, tag: Int) {
    processOp(o.asInstanceOf[RDD[Any]],tag)
  }
  
  def deserializeKey(bytes: BytesWritable) = {    
    keySer.asInstanceOf[Deserializer].deserialize(bytes)
  }

  def deserializeValue(bytes: BytesWritable) = {
    valueSer.asInstanceOf[Deserializer].deserialize(bytes)
  }

  def processOp(rdd: RDD[Any], tag: Int){
    val table_desc = RDDOperator.tableDesc   
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId
    val newRDD = rdd.mapPartitions { iter => {
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      val jc = new JobConf()
      RDDOperator.getTopOperators(top_op).foreach(_.initialize(jc,oi))
      val current_op = top_op.findOperator(current_id).asInstanceOf[RDDReduceSinkOperator]
      //var rowInspector = current_op.getInputObjInspectors()(tag)    
      var rowInspector = current_op.getInputObjInspectors()(0)    
      val keySer = current_op.keySer
      val valueSer = current_op.valueSer
      val keyObjInspector = current_op.keyObjInspector
      val valueObjInspector = current_op.valObjInspector
      iter.map { row => {
        val key = keySer.serialize(current_op.getKeyEval.map { k => k.evaluate(row) },
          keyObjInspector).asInstanceOf[BytesWritable]
        val value = valueSer.serialize(current_op.getValueEval.map { v => v.evaluate(row) },
          valueObjInspector).asInstanceOf[BytesWritable]
        val keyWritable = new BytesWritable(new Array[Byte](key.getLength))
        val valueWritable = new BytesWritable(new Array[Byte](value.getLength))
        System.arraycopy(key.getBytes, 0, keyWritable.getBytes, 0, key.getLength)
        System.arraycopy(value.getBytes, 0, valueWritable.getBytes, 0, value.getLength)
        (new SerializableWritable(keyWritable), new SerializableWritable(valueWritable))
      }}
    }}
    forward(newRDD.asInstanceOf[RDD[(Any,Any)]])
  }

  def forward(rdd: RDD[(Any,Any)]) {
    setDone(true)
    val child = getChildOperators.get(0)
    
    val parentsDone = child.getParentOperators.foldLeft(true)(_ && _.getDone)
    child match {
      case child: RDDJoinOperator    => child.addInputRDD(joinTag,rdd)
      case child: RDDGroupByOperator => child.addInputRDD(joinTag,rdd)
      case _ => Unit
    }
    if (parentsDone) {
      child.process(rdd, 0)
    }
  }
}

