package shark

import org.apache.hadoop.hive.ql.exec.Utilities.EnumDelegate
import org.apache.hadoop.hive.ql.plan.GroupByDesc
import org.apache.hadoop.hive.ql.plan.PlanUtils.ExpressionTypes

import java.beans.XMLEncoder
import java.beans.XMLDecoder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Serializable


object SharkUtilities{

  def xmlSerialize(o:Serializable): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val e = new XMLEncoder(out)
    // workaround for java 1.5
    e.setPersistenceDelegate(classOf[ExpressionTypes], new EnumDelegate())
    e.setPersistenceDelegate(classOf[GroupByDesc.Mode], new EnumDelegate())
    e.writeObject(o)
    e.close()
    out.toByteArray()
  }

  def xmlDeserialize(bytes:Array[Byte]): Serializable = {
    val d:XMLDecoder = new XMLDecoder(new ByteArrayInputStream(bytes))
    val ret = d.readObject().asInstanceOf[Serializable]
    d.close()
    ret
  }
}
