package com.naumen.scala.forms

import com.naumen.scala.utils.FieldNameGetter
import java.text.SimpleDateFormat
import java.util.Date
import com.naumen.scala.forms.extensions.FieldCustomizers


case class FieldDescription(propertyMap: Map[String, Any])

trait FieldDescriptionAttrs {
  val FieldType: String = "FieldType"
  val ListElementType: String = "ListElementType"
  val Required: String = "Required"
  val Label: String = "Label"
  val Placeholder: String = "Placeholder"

}

object FieldDescription extends FieldDescriptionAttrs

case class FieldDescriptionBuilder[T](propertyMap: Map[String, Any]) {
  def addProperty(key: String, value: Any): FieldDescriptionBuilder[T] = copy(propertyMap = propertyMap + (key -> value))

  def addToCollection(key: String, value: Any) =
    addProperty(key, getProperty(key).map(_.asInstanceOf[Seq[Any]] :+ value).getOrElse(Seq()))

  def getProperty(key: String): Option[Any] = propertyMap.get(key)

  def label(l: String): FieldDescriptionBuilder[T] = addProperty(FieldDescription.Label, l)

  def placeholder(l: String): FieldDescriptionBuilder[T] = addProperty(FieldDescription.Placeholder, l)

  def required: FieldDescriptionBuilder[T] = addProperty(FieldDescription.Required, true)


  def build = FieldDescription(propertyMap: Map[String, Any])
}

case class FormDescription(fields: Map[String, FieldDescription] = Map(), attrs: Map[String, Any] = Map())

case class FormDescriptionBuilder[+T: Manifest](fields: Map[String, FieldDescription] = Map(), attrs: Map[String, Any] = Map()) extends FieldNameGetter {

    type FieldFoo[F] = FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F]
    type FormField[F] = FieldFoo[F] => FormDescriptionBuilder[T]

    def emptyStringBuilder = FieldDescriptionBuilder[String](Map())

  def addFieldDescription(name: String, fb: FieldDescription) = copy(fields = fields + (name -> fb))

  def field[F: Manifest](fieldFoo: T => F)(foo: FieldFoo[F]) =
    fieldWithCustomName($[T](fieldFoo))(foo)

  def fieldOpt[F: Manifest](fieldFoo: T => Option[F])(foo: FieldFoo[F]) =
    fieldWithCustomName($[T](fieldFoo))(foo)

  //TODO fieldFoo используется только для вывода типа T. Сделать вывод типа по-другому и убрать ее.
  def fieldOptNamed[F: Manifest](fieldFoo: T => Option[F], fieldName: String)(foo: FieldFoo[F]) =
    fieldWithCustomName(fieldName)(foo)

  def extend[S >: T](foo: FormDescriptionBuilder[S] => FormDescriptionBuilder[S]): FormDescriptionBuilder[T] = {
    foo(this).asInstanceOf[FormDescriptionBuilder[T]]
  }

  def fieldWithCustomName[F: Manifest](fieldName: String)(foo: FieldFoo[F]) = {

    val initialBuilder = FieldDescriptionBuilder[F](Map()).addProperty(FieldDescription.FieldType, implicitly[Manifest[F]].runtimeClass)

    addFieldDescription(fieldName, foo(initialBuilder).build)
  }

  def boolean(fieldFoo: T => Boolean): FormField[Boolean] = fieldBase[Boolean](fieldFoo)(_.required)

  def string(fieldFoo: T => String): FormField[String] =
    fieldBase[String](fieldFoo)(_.required)

  def stringOpt(fieldFoo: T => Option[String]): FormField[String] =
    fieldBase[String](fieldFoo)(identity)

  def int(fieldFoo: T => Int): FormField[Int] = fieldBase[Int](fieldFoo)(_.required)

  def intOpt(fieldFoo: T => Option[Int]): FormField[Int] = fieldBase[Int](fieldFoo)(identity)
    
  def bigDecimal(fieldFoo: T => BigDecimal): FormField[BigDecimal] = fieldBase[BigDecimal](fieldFoo)(_.required)

  def bigDecimalOpt(fieldFoo: T => Option[BigDecimal]): FormField[BigDecimal] = fieldBase[BigDecimal](fieldFoo)(identity)

  def fieldBase[F: Manifest](fieldFoo: T => Any)
                            (foo: FieldFoo[F])
                            (foo2: FieldFoo[F])
  = fieldWithCustomName[F]($[T](fieldFoo))(foo andThen foo2)

  def seq[A: Manifest](fieldFoo: T => Seq[A])(foo: FieldFoo[Seq[A]]) = {
    val elementType = implicitly[Manifest[A]].runtimeClass

    fieldBase[Seq[A]](fieldFoo)(_.addProperty(FieldDescription.ListElementType, elementType))(foo)
  }

  import FieldCustomizers._
  val defaultDateFormat = new SimpleDateFormat("dd.MM.yyyy").toPattern
  def dateOpt(fieldFoo: T => Option[Date]): FormField[Date] = fieldBase[Date](fieldFoo)(_.format(defaultDateFormat))
  def date(fieldFoo: T => Date): FormField[Date] = fieldBase[Date](fieldFoo)(_.required.format(defaultDateFormat))

  def name(n: String) = copy(attrs = attrs + ("name" -> n))

  def build = FormDescription(fields = fields, attrs = attrs)

}

object FormBuilderDsl {
  def emptyFormBuilder[T: Manifest] = new FormDescriptionBuilder[T]()

  def form[T: Manifest](formFoo: FormDescriptionBuilder[T] => FormDescriptionBuilder[T]) = formFoo(emptyFormBuilder)
}


