package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.hive.ql.exec.LimitOperator


class RDDLimitOperator extends LimitOperator {

  override def processOp(o: Object, tag: Int) {    
    processOp(o.asInstanceOf[RDD[Any]])
  }
  
  def processOp(rdd: RDD[Any]) {
    // TODO: Inefficient, might also need broacast var
    var count = 0
    val limit = this.limit
    val newRDD = rdd.filter { row => {
      val passed = count < limit
      if (passed) count += 1
      passed
    }}
    forward(newRDD, inputObjInspectors(0))
  }
}

