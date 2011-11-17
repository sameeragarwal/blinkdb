package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.FileSinkOperator
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils


class RDDFileSinkOperator extends FileSinkOperator {

  def init(hconf: Configuration) {
    initializeOp(hconf)
  }

  override def processOp(rdd: Object, tag: Int) {
    if (tag == -1) {
      return super.processOp(rdd,0) 
    }
    processOp(rdd.asInstanceOf[RDD[Any]], tag)
  }

  def getSpecPath() = specPath

  def processOp(rdd: RDD[Any], tag: Int): Unit = {
    val table_desc = RDDOperator.tableDesc
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId
    val conf = new SerializableWritable(RDDOperator.hconf)
    val newRDD = rdd.mapPartitions { iter => {
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val op = top_op.findOperator(current_id).asInstanceOf[RDDFileSinkOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      RDDOperator.getTopOperators(op).foreach(_.initialize(conf.value,oi))
      iter.foreach { row => {
        op.processOp(row.asInstanceOf[Object],-1)
      }}
      op.closeOp(false)
      iter
    }}

    newRDD.foreach { _ => Unit }
  }
}

