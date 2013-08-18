package shark.operators

import shark._

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.plan._
import org.apache.hadoop.hive.ql.exec.Operator
import org.apache.hadoop.hive.ql.exec.TableScanOperator
import org.apache.hadoop.hive.ql.exec.Utilities.EnumDelegate
import org.apache.hadoop.hive.ql.exec.OperatorFactory
import org.apache.hadoop.hive.ql.exec.OperatorFactory.OpTuple
import org.apache.hadoop.hive.ql.metadata.Table
import org.apache.hadoop.hive.ql.plan.GroupByDesc
import org.apache.hadoop.hive.ql.plan.PlanUtils.ExpressionTypes
import org.apache.hadoop.hive.ql.plan.TableDesc
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo

import spark._

import java.beans.XMLEncoder
import java.beans.XMLDecoder

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.HashMap
import java.util.ArrayList
import java.util.Arrays

import scala.collection.mutable.Stack
import scala.collection.mutable.Map
import scala.collection.JavaConversions._

object RDDOperator extends Logging {

  val opIdToTypeInfos: HashMap[String, ArrayList[TypeInfo]] = new HashMap[String, ArrayList[TypeInfo]]()

  val opIdToSerializedOp: HashMap[String, Array[Byte]] =
    new HashMap[String, Array[Byte]]()

  var broadcastOps: broadcast.Broadcast[HashMap[String, Array[Byte]]] = null

  var hconf: Configuration = null

  /**
   *Serializes each operator in the operator tree before they have been initialized.
   *Adds each to the opIdToSerializedOp map
   */
  def serializeOperatorTree(op: Operator[_ <: Serializable]) {
    val t = System.currentTimeMillis
    val s = Stack[Operator[_ <: Serializable]]()
    val visited = scala.collection.mutable.Set[Operator[_ <: Serializable]]()
    val emptyList = new java.util.ArrayList[Operator[_ <: Serializable]]()

    s.push(op)
    while(!s.isEmpty) {
      val current = s.pop
      // Initialize operators after all of their parents are initialized
      val parentsInitialized = (current.getParentOperators == null) || 
        (current.getParentOperators forall { visited contains _ })
      if (!parentsInitialized)
        s.push(current)
      else if (!(visited.contains(current))) {
        val prevParents = current.getParentOperators
        val prevChildren = current.getChildOperators
        //current.setParentOperators(emptyList) TODO: Slightly hacky way, serializes operator tree multiple times
        current.setChildOperators(emptyList)
        val serializedCurrent = SharkUtilities.xmlSerialize(current)
        opIdToSerializedOp.put(current.getOperatorId, serializedCurrent)
        current.setParentOperators(prevParents)
        current.setChildOperators(prevChildren)
        visited.add(current)

        current match {
          case operator: RDDReduceSinkOperator => {
            // TODO: Should match on a trait rather than indiv classes
            operator.getChildOperators().foreach { child => child match {
              case op: RDDGroupByOperator => {
                op.reduceSinkConf = operator.getConf
              }
              case op: RDDExtractOperator =>
                op.reduceSinkConf = operator.getConf
              case op: RDDJoinOperator => {
                op.reduceSinkConfs(operator.getConf.getTag) = operator.getConf                
              }
            }}
          }
          case _ => None
        }
      }
      if (current.getChildOperators != null)
        current.getChildOperators.filter(op => !(visited.contains(op))).foreach(s.push(_))
      if (current.getParentOperators != null)
        current.getParentOperators.filter(op => !(visited.contains(op))).foreach(s.push(_))
    }
    logInfo("Operator Tree serialized in " + (System.currentTimeMillis - t) + " ms")
    //broadcastOps = SharkEnv.sc.broadcast(opIdToSerializedOp)
  }
}

trait RDDOperator extends Serializable {
  self: Operator[_ <: Serializable] => 
 
  // Only called on slave nodes
  def initObjectInspector(id: String, typeInfos: ArrayList[TypeInfo], hconf: Configuration) {
/*    val ois = typeInfos.map { ti => 
      ExtendedTypeInfoUtils.getStandardObjectInspectorFromTypeInfo(ti)
    }.toArray*/
    val s = Stack[Operator[_ <: Serializable]]()
    if (self.getParentOperators != null)
      self.getParentOperators.foreach(s.push(_))
    while(!(s.isEmpty)) {
      val current = s.pop
      current match {
        case op: RDDTableScanOperator =>  {
          op.setParentOperators(new ArrayList())
          val table = op.getTableDesc
          val partDesc = op.firstConfPartDesc
          val rowObjectInspector = 
            // Add partition field info to object inspector
            if (partDesc != null) {
              val partProps = partDesc.getProperties()
              val tableDeser = partDesc.getDeserializerClass().newInstance()
              tableDeser.initialize(hconf, partProps)
              val partCols = partProps.getProperty(
                org.apache.hadoop.hive.metastore.api.Constants.META_TABLE_PARTITION_COLUMNS);
              val partNames = new ArrayList[String]
              val partObjectInspectors = new ArrayList[ObjectInspector]
              partCols.trim().split("/").foreach(key => {
                  partNames.add(key)
                  partObjectInspectors.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector)
              })
              val partObjectInspector = ObjectInspectorFactory
                  .getStandardStructObjectInspector(partNames, partObjectInspectors);
              val oiList = Arrays.asList(
                tableDeser.getObjectInspector().asInstanceOf[StructObjectInspector], 
                partObjectInspector.asInstanceOf[StructObjectInspector])
              // new oi is union of table + partition object inspectors
                ObjectInspectorFactory.getUnionStructObjectInspector(oiList)
            } else table.getDeserializer().getObjectInspector()
          op.initialize(hconf, Array(rowObjectInspector))
        }
        case op =>
          op.getParentOperators.foreach(s.push(_))
      } 
    } 
  }
  
  def preProcess() {
    self.getInputObjInspectors().zipWithIndex.foreach {case (oi, i) => 
      //println(self.getOperatorId + " tag: " + i + " oi: " + oi)
    }
    Unit
  }

  def cacheRDD[T](rdd: RDD[T]): RDD[_] = {
    rdd
  }

  // Called on Master node
  def evaluate(): RDD[_] = {
    /*
    val typeInfos = new ArrayList[TypeInfo]()
    this.getInputObjInspectors().foreach { oi =>
      typeInfos.add(ExtendedTypeInfoUtils.
                    getTypeInfoFromObjectInspector(oi))
    }
    RDDOperator.opIdToTypeInfos.put(getOperatorId, typeInfos)
    */
    OperatorTreeCache.get(this) match {
      case Some(rdd) => { 
        processRDD(rdd, true)
      }
      case None => {
        processParents(getParentOperators.asInstanceOf[java.util.List[RDDOperator]])
      }
    }
  } 
  
  def processParents(parents: java.util.List[RDDOperator]): RDD[_] = {
    if (parents == null)
      processRDD(null)
    else if (parents.size() == 1)
      processRDD(parents.get(0).evaluate())
    else {
      val m = new HashMap[Int, RDD[_]]()
      parents.foreach { parent => parent match {
        case p: RDDReduceSinkOperator => 
          m.put(p.getConf.getTag, p.evaluate())
      }}
      self match {
        case op: RDDJoinOperator =>
          op.processRDDs(m)
        case op: RDDUnionOperator =>
          op.processRDDs(m)
        case _ =>
          throw new Exception("Wrong type to call  processRDDs on")
      }
    }
  }

  def processRDD[T](rdd: RDD[T]): RDD[_] = {
    processRDD(rdd, false)
  }
  // Called on Master node
  def processRDD[T](rdd: RDD[T], cached: Boolean): RDD[_] = {
    val serializedHConf = SharkUtilities.xmlSerialize(RDDOperator.hconf)
    setChildOperators(new java.util.ArrayList())
    val id = getOperatorId
    val serializedOp = RDDOperator.opIdToSerializedOp.get(id)
    //println(id + " len " + serializedOp.length)
    val typeInfos = RDDOperator.opIdToTypeInfos.get(getOperatorId)
//    val broadcastOps = RDDOperator.broadcastOps
    rdd.mapPartitions { iter => {
      //val op = SharkUtilities.xmlDeserialize(broadcastOps.value.get(id)).asInstanceOf[RDDOperator]
      val op = SharkUtilities.xmlDeserialize(serializedOp).asInstanceOf[RDDOperator]
      val hconf = SharkUtilities.xmlDeserialize(serializedHConf).asInstanceOf[Configuration]
      op.initObjectInspector(id, typeInfos, hconf)
      op.preProcess()
      val newIter = op.processIter(iter)
      op.postProcess()
      newIter
    }}
  }

  def processIter[T](iter: Iterator[T]): Iterator[_] = {
    iter
  }

  def postProcess() {
    Unit
  }
}
