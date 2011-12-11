package shark

import org.apache.hadoop.hive.serde2.typeinfo._
import org.apache.hadoop.hive.serde2.objectinspector._
import org.apache.hadoop.hive.serde2.objectinspector.primitive._
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category
import java.util.ArrayList
import scala.collection.JavaConversions._

object ExtendedTypeInfoUtils {
  

  def getStandardObjectInspectorFromTypeInfo(ti: TypeInfo): ObjectInspector = {
    ti match {
      case typeInfo: ExtendedPrimitiveTypeInfo => {
        if (typeInfo.isJavaType)
          PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(typeInfo.getPrimitiveCategory)
        else
          PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(typeInfo.getPrimitiveCategory)
      }
      case typeInfo: ListTypeInfo => {
        val elementOI = getStandardObjectInspectorFromTypeInfo(typeInfo.getListElementTypeInfo)
        ObjectInspectorFactory.getStandardListObjectInspector(elementOI)
      }
      case typeInfo: MapTypeInfo => {
        val keyOI = getStandardObjectInspectorFromTypeInfo(typeInfo.getMapKeyTypeInfo)
        val valueOI = getStandardObjectInspectorFromTypeInfo(typeInfo.getMapValueTypeInfo)
        ObjectInspectorFactory.getStandardMapObjectInspector(keyOI, valueOI)
      }
      case typeInfo: StructTypeInfo => {
        val fieldNames = typeInfo.getAllStructFieldNames
        val fieldTypeInfos = typeInfo.getAllStructFieldTypeInfos
        val fieldOIs = new ArrayList[ObjectInspector](fieldTypeInfos.size)
        fieldTypeInfos.foreach(ti => 
          fieldOIs.add(getStandardObjectInspectorFromTypeInfo(ti)))
        ObjectInspectorFactory.getStandardStructObjectInspector(
          fieldNames,
          fieldOIs)
      }
      case typeInfo: UnionTypeInfo => {
        val objectTypeInfos = typeInfo.getAllUnionObjectTypeInfos
        val fieldOIs = new ArrayList[ObjectInspector](objectTypeInfos.size)
        objectTypeInfos.foreach(ti =>
          fieldOIs.add(getStandardObjectInspectorFromTypeInfo(ti)))
        ObjectInspectorFactory.getStandardUnionObjectInspector(
          fieldOIs)
      }
      case _ => null
    }
  }

  def getTypeInfoFromObjectInspector(objectInspector: ObjectInspector): TypeInfo = {
    objectInspector match {
      case oi: AbstractPrimitiveWritableObjectInspector =>
        new ExtendedPrimitiveTypeInfo(oi.getTypeName, false)
      case oi: AbstractPrimitiveJavaObjectInspector =>
        new ExtendedPrimitiveTypeInfo(oi.getTypeName, true)
      case oi: ListObjectInspector =>
        TypeInfoFactory.getListTypeInfo(
          getTypeInfoFromObjectInspector(oi.getListElementObjectInspector))
      case oi: MapObjectInspector =>
        TypeInfoFactory.getMapTypeInfo(
          getTypeInfoFromObjectInspector(oi.getMapKeyObjectInspector),
          getTypeInfoFromObjectInspector(oi.getMapValueObjectInspector))
      case oi: StructObjectInspector => {
        val fields = oi.getAllStructFieldRefs
        val fieldNames = new ArrayList[String](fields.size)
        val fieldTIs = new ArrayList[TypeInfo](fields.size)
        fields.foreach((f: StructField) => { 
          fieldNames.add(f.getFieldName)
          fieldTIs.add(getTypeInfoFromObjectInspector(f.getFieldObjectInspector))
        })
        TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTIs)
      }
      case oi: UnionObjectInspector => {
        val objectTIs = new ArrayList[TypeInfo]()
        oi.getObjectInspectors.foreach(eoi => {
          objectTIs.add(getTypeInfoFromObjectInspector(eoi))
        })
        TypeInfoFactory.getUnionTypeInfo(objectTIs)
      }
    }   
  }  
}
