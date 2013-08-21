package com.naumen.scala.forms

import play.api.data._
import play.api.data.validation.{Constraints, Constraint}
import play.api.data.FormError
import com.naumen.scala.utils.{FieldNameGetter, ClassConverter}

case class MappingFieldBuilder[T](fieldMapping: Mapping[T], propertyMap: Map[String, Any]) extends Mapping[T]{
  type Self = MappingFieldBuilder[T]

  def addProperty(key: String, value: Any) = copy(propertyMap = propertyMap + (key -> value) )

  def addToCollection(key: String, value: Any) =
    addProperty(key, getProperty(key).map(_.asInstanceOf[Seq[Any]] :+ value).getOrElse(Seq()) )

  def getProperty(key: String): Option[Any]   = propertyMap.get(key)

  val key: String = fieldMapping.key
  val mappings: Seq[Mapping[_]] = fieldMapping.mappings
  val constraints: Seq[Constraint[T]] = fieldMapping.constraints

  def bind(data: Map[String, String]): Either[Seq[FormError], T] = fieldMapping.bind(data)

  def unbind(value: T): (Map[String, String], Seq[FormError])  = fieldMapping.unbind(value)

  def withPrefix(prefix: String): Self   = copy(fieldMapping = fieldMapping.withPrefix(prefix))

  def verifying(constraints: Constraint[T]*): Self   = copy(fieldMapping = fieldMapping.verifying(constraints:_*))
}

object FieldBuilderExtenders {
   implicit class StringFieldBuilderExtender(val fb: MappingFieldBuilder[String]) {
     def maxSize(s: Int):MappingFieldBuilder[String] = fb.verifying(Constraints.maxLength(s))
   }

}

case class FormBuilder[T:Manifest](fields: Map[String, MappingFieldBuilder[_]] = Map()) extends FieldNameGetter{
  import play.api.data.Forms._
  def emptyStringBuilder = MappingFieldBuilder[String](text, Map())

  def addFieldBuilder(name: String, fb: MappingFieldBuilder[_]) =    copy(fields  = fields + (name -> fb))

  def field[F](mapping: Mapping[F])(fieldFoo: T => F)(foo: MappingFieldBuilder[F] => MappingFieldBuilder[F]) = {
    addFieldBuilder($[T](fieldFoo), foo(MappingFieldBuilder[F](mapping, Map())))
  }

  def  string(fieldFoo: T => String)(foo: MappingFieldBuilder[String] => MappingFieldBuilder[String]): FormBuilder[T]   =
    field(text)(fieldFoo)(foo)


  def build: ExtendedForm[T] =
    new ExtendedForm[T](fields)

}

object FormBuilderDsl {
  def emptyFormBuilder[T:Manifest] = new FormBuilder[T]()
  def form[T:Manifest](formFoo: FormBuilder[T] => FormBuilder[T]) = formFoo(emptyFormBuilder)
}

case   class FormMapping[T: Manifest]( fieldMappings: Map[String, Mapping[Any]],key: String = "", constraints: Seq[Constraint[T]] = Nil)
  extends Mapping[T] with ObjectMapping{
  def bind(data: Map[String, String]): Either[Seq[FormError], T] = {
    val (errors, values) = fieldMappings.map{case (name, mapping) => {
      name -> mapping.withPrefix(name).bind(data)
    }}.partition{case (_, either) => either.isLeft}

    if (errors.nonEmpty) {
      Left(errors.flatMap{case (_, leftError) => leftError.left.get}.toSeq)
    }  else {
      val valuesMap = values.mapValues(_.right.get)
      Right(restoreEntity(valuesMap))
    }
  }

  def restoreEntity(valuesMap: Map[String, Any]): T = {
    val preparedMap = valuesMap.mapValues(_.asInstanceOf[AnyRef])
    ClassConverter.toInstanceOf[T](preparedMap)
  }

  def unbind(entity: T): (Map[String, String], Seq[FormError]) = {
    val fieldValues = toFieldValuesMap(entity)
    fieldMappings.map{
      case (name, mapping) =>
        val value: Any = fieldValues.get(name).get
        mapping.unbind(value)
    }.foldLeft((Map[String, String](), Seq[FormError]())){
      case (a, (valueMap, errors)) => (a._1 ++ valueMap) -> (a._2 ++ errors)
    }
  }

  def toFieldValuesMap(entity: T): Map[String, Any] = {
    ClassConverter.toMap(entity.asInstanceOf[AnyRef])
  }

  def withPrefix(prefix: String): Mapping[T] = this.copy(key = addPrefix(prefix).getOrElse(key))

  def verifying(addConstraints: Constraint[T]*) = {
    this.copy(constraints = constraints ++ addConstraints.toSeq)
  }

  val mappings: scala.Seq[play.api.data.Mapping[_]] = Seq(this) ++ fieldMappings.map{case (name, mapping) => mapping}
}
