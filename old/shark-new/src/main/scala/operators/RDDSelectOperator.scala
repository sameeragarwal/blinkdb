package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.SelectOperator

import scala.collection.JavaConversions._

class RDDSelectOperator extends SelectOperator with RDDOperator {

  def getEval() = eval

  override def processIter[T](iter: Iterator[T]) = {
    if (conf.isSelStarNoCompute())
      iter
    else {
      val eval = getEval
      iter.map { row =>  {
        //println("select " + row + " " + eval.map(x => x.evaluate(row)))
eval.map(x => x.evaluate(row)) }}
    }
  }
}

