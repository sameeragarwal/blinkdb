package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.SelectOperator
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.mapred.JobConf

import scala.collection.JavaConversions._
import scala.collection.mutable.Stack


class RDDSelectOperator extends SelectOperator {

  def init(hconf: Configuration) {
    initializeOp(hconf)
  }

  override def processOp(rdd: Object, tag: Int) {
    val newRDD = processRDD(rdd.asInstanceOf[RDD[Any]])
    rdd.asInstanceOf[RDD[Any]]
    forward(newRDD, inputObjInspectors(tag))
  }

  def getEval() = eval

  def processRDD[T](rdd: RDD[T]): Any = {
    if (conf.isSelStarNoCompute()){
      return rdd
    }
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId
    val table_desc = RDDOperator.tableDesc   
    val newRDD = rdd.mapPartitions { iter => {
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val jc = new JobConf()
      val current_op = top_op.findOperator(current_id).asInstanceOf[RDDSelectOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      RDDOperator.getTopOperators(top_op).foreach(_.initialize(jc,oi))
      val eval = current_op.getEval
      iter.map { row => eval.map(x => x.evaluate(row)) }
    }}
    newRDD
  }
}

