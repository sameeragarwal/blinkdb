package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory
import org.apache.hadoop.hive.ql.exec.FilterOperator
import org.apache.hadoop.hive.ql.metadata.HiveException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector

class RDDFilterOperator extends FilterOperator with RDDOperator{

  @transient var conditionEvaluator: ExprNodeEvaluator = null

  override def initializeOp(hconf: Configuration) {
    try {
      conditionEvaluator = ExprNodeEvaluatorFactory.get(conf.getPredicate())
    } catch {
      case e: Throwable => throw new HiveException(e)
    }
    initializeChildren(hconf);
  }

  def getConditionEvaluator = conditionEvaluator

  override def processIter[T](iter: Iterator[T]) = {
    val rowInspector = getInputObjInspectors()(0)
    val conditionInspector = getConditionEvaluator.initialize(rowInspector)
      .asInstanceOf[PrimitiveObjectInspector]

    iter.filter { row => {
      //println("filter value " + row)
      java.lang.Boolean.TRUE.equals(conditionInspector.getPrimitiveJavaObject(
        getConditionEvaluator.evaluate(row))) }
    }
  }
}


