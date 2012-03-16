package shark.operators

import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.io.Text
import org.apache.hadoop.io.BytesWritable
import org.apache.hadoop.hive.ql.exec.FileSinkOperator
import org.apache.hadoop.hive.serde2.Serializer
import scala.reflect.BeanProperty

class RDDFileSinkOperator extends FileSinkOperator with RDDOperator {
  
  @BeanProperty
  var isCTAS = false
  @BeanProperty
  var ctasTableName: String = null
  val CACHED_SUFFIX = "_cached"
  def cacheOutputTable() = {
    isCTAS && ctasTableName.endsWith(CACHED_SUFFIX)
  }
  
  override def processRDD[T](rdd: RDD[T], cached: Boolean): RDD[_] = {
    if (!cacheOutputTable)
      super.processRDD(rdd, false)
    else {
      val serializedRDD = super.processRDD(rdd, false)
      serializedRDD.cache()
      OperatorTreeCache.put(this, serializedRDD)
      serializedRDD
    }
  }

  override def processIter[T](iter: Iterator[T]) = {
    if (!cacheOutputTable) {
      iter.foreach { row => {
        processOp(row, 0)
      }}
      closeOp(false)
      iter
    }
    else { //Serialize data to be cached
      val tableDesc = conf.getTableInfo
      val serializer = tableDesc.getDeserializerClass.newInstance().asInstanceOf[Serializer]
      serializer.initialize(null, tableDesc.getProperties)
      iter.map { row => { //Can't have partitions in CTAS output
        val v = serializer.serialize(row, inputObjInspectors(0))
        //Need to copy serialized value b/c its byte array will be reused
        v match {
          case v: BytesWritable =>
            new BytesWritable(v.getBytes)
          case v: Text =>
            new Text(v)
        }
      }}
    }
  }
}

