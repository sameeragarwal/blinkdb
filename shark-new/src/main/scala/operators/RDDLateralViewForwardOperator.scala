package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hive.ql.exec.LateralViewForwardOperator
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils

import java.util.ArrayList

import scala.collection.JavaConversions._


class RDDLateralViewForwardOperator extends LateralViewForwardOperator {

  override def processOp(rdd: Object, tag: Int) {
    val newRDD = processRDD(rdd.asInstanceOf[RDD[Any]])
    forward(newRDD, inputObjInspectors(tag))
  }

  /*
   * This method implements an explode UDTF (Hive does this from the 
   * UDTFOperator class instead). Returns RDD with a new row for each 
   * value of each row's array to be exploded.
   */
  def processRDD[T: ClassManifest](rdd: RDD[T]) = {
    val selOpId = getChildOperators.asInstanceOf[ArrayList[Any]](1)
      .asInstanceOf[RDDSelectOperator].getOperatorId
    val ser_top_op = RDDOperator.serOp
    val table_desc = RDDOperator.tableDesc

    val newRDD = rdd.mapPartitions { iter => {
      // Get eval method from SELECT op to find field that needs to be exploded
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      val jc = new JobConf()
      RDDOperator.getTopOperators(top_op).foreach(_.initialize(jc, oi))
      val selOp = top_op.findOperator(selOpId).asInstanceOf[RDDSelectOperator]
      val eval = selOp.getEval 
     
      iter.flatMap { row => {
        // Start with array of values to be exploded and add row data to each
        val newRows = eval(0).evaluate(row).asInstanceOf[ArrayList[Any]].toArray.map { e => {
          val newRow = row.asInstanceOf[ArrayList[Any]].clone().asInstanceOf[ArrayList[Any]]
          newRow.add(e)
          newRow
        }}
        newRows
      }}
    }}
    newRDD
  }
}

