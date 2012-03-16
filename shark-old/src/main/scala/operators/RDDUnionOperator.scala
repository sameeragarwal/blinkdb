package shark.operators

import spark._
import org.apache.hadoop.hive.ql.exec.UnionOperator
import java.util.HashMap

import scala.collection.JavaConversions._

// Doesn't do any conversion (e.g. if one side is writable while the other side is java, it fails)
// Currently only unions two sets
class RDDUnionOperator extends UnionOperator with RDDOperator {
  def processRDDs(rdds: HashMap[Int, RDD[_]]): RDD[_] = {
    rdds.get(0).asInstanceOf[RDD[Any]] ++ rdds.get(1).asInstanceOf[RDD[Any]]
  }
}
