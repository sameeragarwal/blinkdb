package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.ql.exec.LimitOperator


class RDDLimitOperator extends LimitOperator with RDDOperator {

  override def processIter[T](iter: Iterator[T]) = {
    var count = 0
    val limit = this.limit
    iter.filter { row => {
      val passed = count < limit
      if (passed) count += 1
      passed
    }}
  }
}

