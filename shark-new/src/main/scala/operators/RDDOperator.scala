package shark.operators

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.Operator
import org.apache.hadoop.hive.ql.exec.TableScanOperator
import org.apache.hadoop.hive.ql.exec.Utilities.EnumDelegate
import org.apache.hadoop.hive.ql.metadata.Table
import org.apache.hadoop.hive.ql.plan.GroupByDesc
import org.apache.hadoop.hive.ql.plan.PlanUtils.ExpressionTypes
import org.apache.hadoop.hive.ql.plan.TableDesc

import java.beans.XMLEncoder
import java.beans.XMLDecoder

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.HashMap

import scala.collection.mutable.Stack
import scala.collection.JavaConversions._


object RDDOperator {

  var topOp: RDDTableScanOperator = null

  var topOperators: RDDTableScanOperator = null

  var topToTable: HashMap[TableScanOperator, Table] = null

  var tableDesc: TableDesc = null

  var serOp: Array[Byte] = null

  var hconf: Configuration = null

  def serializeOperator(o: Operator[_ <: Serializable]): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val e = new XMLEncoder(out);
    // workaround for java 1.5
    e.setPersistenceDelegate(classOf[ExpressionTypes], new EnumDelegate());
    e.setPersistenceDelegate(classOf[GroupByDesc.Mode], new EnumDelegate());
    e.writeObject(o);
    e.close();
    out.toByteArray()
  }

  def deserializeOperator(bytes: Array[Byte]) = {
    val d: XMLDecoder = new XMLDecoder(new ByteArrayInputStream(bytes))
    var ret: Operator[_ <: Serializable] = null
    ret = d.readObject().asInstanceOf[Operator[_ <: Serializable]]
    d.close();
    ret
  }

  def getTopOperators(op: Operator[_ <: Serializable]): List[TableScanOperator] = {
    val s = Stack[Operator[_ <: Serializable]]()
    var top = List[TableScanOperator]()
    val visited = scala.collection.mutable.Set[Operator[_ <: Serializable]]()
    s.push(op)
    while(!s.isEmpty) {
      val current = s.pop
      visited.add(current)
      if (current.getParentOperators == null || current.getParentOperators.isEmpty)
        top = current.asInstanceOf[TableScanOperator] :: top
      else 
        current.getParentOperators.filter(op => !(visited.contains(op))).foreach(s.push(_))

      if (current.getChildOperators != null)
        current.getChildOperators.filter(op => !(visited.contains(op))).foreach(s.push(_))
    }
    top
  }

  def initTopOps(op: Operator[_ <: Serializable]): Unit = {
    val topOps = getTopOperators(op)
    topOps.foreach(op => {
      op.initialize(hconf, Array(topToTable.get(topOp).getDeserializer.getObjectInspector))
    })
  }
}

