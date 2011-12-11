package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.FileSinkOperator


class RDDFileSinkOperator extends FileSinkOperator with RDDOperator {

  override def processIter[T](iter: Iterator[T]) = {
    iter.foreach { row => {
      processOp(row, 0)
    }}
    closeOp(false)
    iter
  }
}

