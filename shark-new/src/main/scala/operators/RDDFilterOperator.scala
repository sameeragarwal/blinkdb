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


class RDDFilterOperator extends FilterOperator {

  @transient var conditionEvaluator: ExprNodeEvaluator = null

  // TODO(rxin): any reason this is declared here?
  @transient var conditionInspector: PrimitiveObjectInspector = null

  def init(hconf: Configuration) {
    initializeOp(hconf)
  }

  override def initializeOp(hconf: Configuration) {
    try {
      conditionEvaluator = ExprNodeEvaluatorFactory.get(conf.getPredicate())
    } catch {
      case e: Throwable => throw new HiveException(e)
    }
    initializeChildren(hconf);
  }

  def getConditionEvaluator = conditionEvaluator

  override def processOp(rdd: Object, tag: Int): Unit = {
    val newRDD = OperatorTreeCache.get(this) match {
      case Some(r) => { 
        println("Found filter op in cache")
        r
      }
      case None => {
        println("Didn't find filter op in cache")
        val r = processRDD(rdd.asInstanceOf[RDD[Any]])
        r.cache()
        OperatorTreeCache.put(this, r)
        r
      }
    }
    forward(newRDD, inputObjInspectors(tag))
  }

  def processRDD[T: ClassManifest](rdd: RDD[T]) = {
    val table_desc = RDDOperator.tableDesc
    val ser_top_op = RDDOperator.serOp
    val current_id = this.getOperatorId
    rdd.mapPartitions { iter => {
      val jc = new JobConf()
      val top_op = RDDOperator.deserializeOperator(ser_top_op).asInstanceOf[RDDTableScanOperator]
      val current_op = top_op.findOperator(current_id).asInstanceOf[RDDFilterOperator]
      val oi = Array(ObjectInspectorUtils.getStandardObjectInspector(
        table_desc.getDeserializer().getObjectInspector()))
      RDDOperator.getTopOperators(top_op).foreach { _.initialize(jc, oi) }
      var rowInspector = current_op.getInputObjInspectors()(0)
      var conditionInspector = current_op.getConditionEvaluator.initialize(rowInspector)
                               .asInstanceOf[PrimitiveObjectInspector];

      // TODO(rxin): Can't this be more concise?
      iter.filter { row =>
        java.lang.Boolean.TRUE.equals(
          conditionInspector.getPrimitiveJavaObject(current_op.getConditionEvaluator.evaluate(row)))
      }
    }}
  }
}

