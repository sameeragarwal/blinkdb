package shark.operators

import shark._
import spark.{Serializer => _,_}
import spark.SparkContext._

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.Writable
import org.apache.hadoop.io.Text
import org.apache.hadoop.hive.ql.exec.Operator
import org.apache.hadoop.hive.ql.exec.TableScanOperator
import org.apache.hadoop.hive.ql.exec.Utilities
import org.apache.hadoop.hive.ql.metadata.Table
import org.apache.hadoop.hive.ql.metadata.Partition;
import org.apache.hadoop.hive.ql.optimizer.ppr.PartitionPruner
import org.apache.hadoop.hive.ql.parse.ParseContext
import org.apache.hadoop.hive.ql.parse.PrunedPartitionList
import org.apache.hadoop.hive.ql.plan.PartitionDesc
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo
import org.apache.hadoop.hive.serde2.`lazy`.ByteArrayRef
import org.apache.hadoop.hive.serde2.`lazy`.LazyFactory
import org.apache.hadoop.hive.serde2.`lazy`.LazyStruct
import org.apache.hadoop.hive.serde2.`lazy`.LazyObject

import java.io.Serializable
import java.util.ArrayList
import java.util.Arrays

import scala.collection.JavaConversions._
import scala.collection.mutable.Stack
import scala.reflect.BeanProperty

class RDDTableScanOperator extends TableScanOperator with RDDOperator {

  @transient var table: Table = _
  @BeanProperty
  var partsList: PrunedPartitionList = _
  @BeanProperty
  var firstConfPartDesc: PartitionDesc  = _
  @BeanProperty
  var numConfirmedParts: Int = _

/*  override def cacheRDD[T](rdd: RDD[T]): RDD[_] = {
    rdd.cache()
    OperatorTreeCache.put(this, rdd)
    rdd
  }*/

  override def processRDD[T](rdd: RDD[T], cached: Boolean): RDD[_] = {
    val newRDD = 
      if (numConfirmedParts > 0) {
        var rowsRDD: RDD[Object] = null
        val partitions = partsList.getConfirmedPartns().toArray()

        partitions.foreach(part => {
          val partition = part.asInstanceOf[Partition]
          val partDesc = Utilities.getPartitionDesc(partition)
          val partRDD = (table.getInputFormatClass() 
            match {
              case _ =>  SharkEnv.sc.textFile(partition.getDataLocation().getPath()) 
             })
            .map(value => {
              // Map each tuple to a row object
               val deserializer = partDesc.getDeserializer()
               val oi = deserializer.getObjectInspector().asInstanceOf[StructObjectInspector]

               // Get table field ois
               val ois = new ArrayList[ObjectInspector]
               val tableFields= oi.asInstanceOf[StructObjectInspector].getAllStructFieldRefs()
               tableFields.foreach(sf => ois.add(sf.getFieldObjectInspector()))

               // Get partition field info
               val partSpec = partDesc.getPartSpec()
               val partProps = partDesc.getProperties()
               val deser = partDesc.getDeserializer()
               val rawRowObjectInspector = deser.getObjectInspector()
               val partCols = partProps.getProperty(
                 org.apache.hadoop.hive.metastore.api.Constants.META_TABLE_PARTITION_COLUMNS);
               val partKeys = partCols.trim().split("/")
               val partValues = new ArrayList[String]
               partKeys.foreach(key => {
                 if (partSpec == null) {
                   partValues.add(new String)
                 } else {
                   partValues.add(new String(partSpec.get(key)))
                 }
               })

               val deserializedRow = deserializer.deserialize(value) // LazyStruct
               val rowWithPartArr = new Array[Object](2)
               rowWithPartArr.update(0, deserializedRow)
               rowWithPartArr.update(1, partValues)
               rowWithPartArr.asInstanceOf[Object]
             })
            rowsRDD = rowsRDD match  {
              case null => partRDD
              case _ => rowsRDD.union(partRDD)
            }
        })
        rowsRDD
      } else {
        val tablePath = table.getDataLocation.toString
        getTableDesc.getInputFileFormatClass match {
          case _ => SharkEnv.sc.textFile(tablePath)
        }
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
    iter.map { value => 
      value match { 
        case rowWithPart: Array[Object] => rowWithPart
        case v: String  => deserializer.deserialize(v) 
        case v: Text => deserializer.deserialize(v)
        case v: Writable => deserializer.deserialize(v)
      }
    }
  }
}

