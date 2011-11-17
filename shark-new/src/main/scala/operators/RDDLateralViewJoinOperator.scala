package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.LateralViewJoinOperator


class RDDLateralViewJoinOperator extends LateralViewJoinOperator {
  val SELECT_TAG = 0
  val UDTF_TAG = 1

  override def processOp(rdd: Object, tag: Int) {
    if (tag == SELECT_TAG) {
      val newRDD = rdd.asInstanceOf[RDD[Any]]
      forward(newRDD, outputObjInspector)
    }
  }
}

