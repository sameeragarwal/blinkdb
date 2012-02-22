package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.UDTFOperator


class RDDUDTFOperator extends UDTFOperator {
  override def processOp(rdd: Object, tag: Int) {
    // Don't forward anything; explode is implemented in the LVF operator.
  }
}

