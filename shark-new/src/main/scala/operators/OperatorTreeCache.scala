package shark.operators

import shark.SharkUtilities
import spark._

import org.apache.hadoop.hive.ql.exec._

import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._

import java.util.Arrays


object OperatorTreeCache {
  val opToRDD = new HashMap[AnyRef,RDD[_]]()
  def makeKeyWrapper(operator: Operator[_]): KeyWrapper = {
    new KeyWrapper(getKeyList(operator,List()))
  }
  // TODO: Can make this an instance method of RDDOperator. Also need to remove duplicate serialization.
  def getKeyList(operator: Operator[_], l: List[Any]): List[Any] = {
    operator match {
      case op: RDDTableScanOperator => {
        println("table scan : " + op.getTableDesc.getTableName.split('.')(1))
        op.getTableDesc.getTableName.split('.')(1) :: l
      }
      case op: RDDSelectOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDFilterOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDFileSinkOperator => {
        if (op.cacheOutputTable)
          op.ctasTableName :: l
        else
          getKeyList(op.getParentOperators()(0),
                     new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      }
      case op: RDDReduceSinkOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDLimitOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDUnionOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDGroupByOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDExtractOperator =>
        getKeyList(op.getParentOperators()(0),
                   new String(SharkUtilities.xmlSerialize(op.getConf)) :: l)
      case op: RDDJoinOperator => // Assumes 2 parents
        getKeyList(op.getParentOperators()(1),
                   getKeyList(op.getParentOperators()(1),
                              new String(SharkUtilities.xmlSerialize(op.getConf)) :: l))

    }
  }
  def put(operator: Operator[_],rdd:RDD[_]) {
    opToRDD.put(makeKeyWrapper(operator),rdd)
  }
  def get(operator: Operator[_]): Option[RDD[_]] = {
    opToRDD.get(makeKeyWrapper(operator))
  }
}

class KeyWrapper(val keys: List[Any]) {
  
  override def hashCode():Int = {
    keys.hashCode
  }
  override def equals(other: Any): Boolean = {
    other match {
      case that: KeyWrapper =>
        that.keys.equals(keys)
      case _ => false
    }
  }
}

