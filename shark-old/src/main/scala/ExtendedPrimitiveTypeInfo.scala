package shark


import org.apache.hadoop.hive.serde2.typeinfo._
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils

/*
 * Based on Hive PrimitiveTypeInfo. This TypeInfo stores whether or not the 
 * primitive is a writable or java object.
 */

@serializable final class ExtendedPrimitiveTypeInfo(typeName: String, val isJavaType: Boolean) extends TypeInfo {
  
  override def getCategory(): Category = {
    Category.PRIMITIVE
  }
  def getPrimitiveCategory() = PrimitiveObjectInspectorUtils.getTypeEntryFromTypeName(typeName).primitiveCategory
  override def getTypeName() = typeName
  override def equals(other: Any) = (this eq other.asInstanceOf[AnyRef])
  override def hashCode() = typeName.hashCode
}
