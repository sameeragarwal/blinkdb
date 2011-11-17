package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.Operator
import org.apache.hadoop.hive.ql.exec.TableScanOperator

import java.io.Serializable

import scala.collection.JavaConversions._
import scala.collection.mutable.Stack


class RDDTableScanOperator extends TableScanOperator {

  override def processOp(obj: Object, tag: Int) {
    val rdd = obj.asInstanceOf[RDD[Any]]
    forward(rdd, inputObjInspectors(tag))
  }

  def processRDD[T](rdd: RDD[T]) = {
    rdd
  }

  def findOperator(operatorId: String): Operator[_ <: Serializable] = {
    val visited = scala.collection.mutable.Set[Operator[_ <: Serializable]]()
    val remaining: Stack[Operator[_ <: Serializable]] = Stack(this)
    var current: Operator[_ <: Serializable] = null
    while (!remaining.isEmpty) {
      current = remaining.pop
      visited.add(current)
      if (current.getOperatorId == operatorId)
        return current
      if (current.getChildOperators != null)
        current.getChildOperators.filter(op => !(visited.contains(op))).foreach(remaining.push(_))
      if (current.getParentOperators != null)
        current.getParentOperators.filter(op => !(visited.contains(op))).foreach(remaining.push(_))
    }
    null
  }
}

