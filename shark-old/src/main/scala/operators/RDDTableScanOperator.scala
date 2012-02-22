package shark.operators

import shark._
import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.io.Writable
import org.apache.hadoop.hive.ql.exec.Operator
import org.apache.hadoop.hive.ql.exec.TableScanOperator
import org.apache.hadoop.hive.ql.metadata.Table
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo
import org.apache.hadoop.hive.serde2.`lazy`.ByteArrayRef
import org.apache.hadoop.hive.serde2.`lazy`.LazyFactory

import java.io.Serializable
import java.util.ArrayList

import scala.collection.JavaConversions._
import scala.collection.mutable.Stack


class RDDTableScanOperator extends TableScanOperator with RDDOperator {

  @transient var table: Table = _

/*  override def cacheRDD[T](rdd: RDD[T]): RDD[_] = {
    rdd.cache()
    OperatorTreeCache.put(this, rdd)
    rdd
  }*/

  override def processRDD[T](rdd: RDD[T], cached: Boolean): RDD[_] = {
    val tablePath = table.getDataLocation.toString
    val newRDD = getTableDesc.getInputFileFormatClass match {
      case _ => SharkEnv.sc.textFile(tablePath)
    }
    if (!cached) {
      newRDD.cache()
      OperatorTreeCache.put(this, newRDD)
      super.processRDD(newRDD, false)
    }
    else
      super.processRDD(rdd, false)
  }

  override def processIter[T](iter: Iterator[T]): Iterator[_] = {
    val deserializer = getTableDesc.getDeserializer
    val oi = deserializer.getObjectInspector.asInstanceOf[StructObjectInspector]
    iter.map { value => {
      deserializer.deserialize(value.asInstanceOf[String])
    }}
  }
}

