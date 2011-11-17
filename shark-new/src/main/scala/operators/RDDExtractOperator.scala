package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.exec.ExtractOperator
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory
import org.apache.hadoop.io.BytesWritable
import org.apache.hadoop.mapred.JobConf

import scala.collection.JavaConversions._


class RDDExtractOperator extends ExtractOperator {

  def getEval = eval

  override def initializeOp(hconf:Configuration) {
    eval = ExprNodeEvaluatorFactory.get(conf.getCol)
    outputObjInspector = eval.initialize(
      inputObjInspectors(0).asInstanceOf[StructObjectInspector]
        .getStructFieldRef("VALUE").getFieldObjectInspector)
    initializeChildren(hconf)
  }

  override def processOp(o:Object, tag:Int){    
    processOp(o.asInstanceOf[RDD[Any]])
  }

  def processOp(rdd:RDD[Any]){

    val table_desc = RDDOperator.tableDesc   
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId
    val newRDD = rdd.mapPartitions { iter => {
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      val jc = new JobConf()
      RDDOperator.getTopOperators(top_op).foreach(_.initialize(jc,oi))
      val current_op = top_op.findOperator(current_id).asInstanceOf[RDDExtractOperator]
      val rowInspector = current_op.getInputObjInspectors()(0).asInstanceOf[StructObjectInspector]
      val parent = current_op.getParentOperators()(0).asInstanceOf[RDDReduceSinkOperator]
      
      iter.map { row => {
        row match {
          case (key: SerializableWritable[BytesWritable],
            value: SerializableWritable[BytesWritable]) => { 
            current_op.getEval.evaluate(parent.deserializeValue(value.value))
          }
          case _ => throw new Exception
        }
      }}
    }}

    forward(newRDD,inputObjInspectors(0))
  }
}

