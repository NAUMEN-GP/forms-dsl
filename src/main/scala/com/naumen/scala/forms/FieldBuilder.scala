package com.naumen.scala.forms

import com.naumen.scala.utils.FieldNameGetter


case class FieldDescription(propertyMap: Map[String, Any])

object FieldDescription {
  val FieldType: String = "FieldType"
  val ListElementType: String = "ListElementType"
  val Required: String = "_required"
  val Label: String = "_label"

}

case class FieldDescriptionBuilder[T](propertyMap: Map[String, Any]) {
  def addProperty(key: String, value: Any): FieldDescriptionBuilder[T] = copy(propertyMap = propertyMap + (key -> value))

  def addToCollection(key: String, value: Any) =
    addProperty(key, getProperty(key).map(_.asInstanceOf[Seq[Any]] :+ value).getOrElse(Seq()))

  def getProperty(key: String): Option[Any] = propertyMap.get(key)

  def label(l: String): FieldDescriptionBuilder[T] = addProperty(FieldDescription.Label, l)

  def required: FieldDescriptionBuilder[T] = addProperty(FieldDescription.Required, true)


  def build = FieldDescription(propertyMap: Map[String, Any])
}

object FieldBuilderExtenders {

  implicit class StringFieldBuilderExtender(val fb: FieldDescriptionBuilder[String]) {
    def maxSize(s: Int): FieldDescriptionBuilder[String] = fb.addProperty("_maxSize", s)

    def label(l: String): FieldDescriptionBuilder[String] = fb.addProperty("_label", l)
  }

}

case class FormDescription(fields: Map[String, FieldDescription] = Map(), attrs: Map[String, Any] = Map())

case class FormDescriptionBuilder[+T: Manifest](fields: Map[String, FieldDescription] = Map(), attrs: Map[String, Any] = Map()) extends FieldNameGetter {

  //    import play.api.data.Forms._

  def emptyStringBuilder = FieldDescriptionBuilder[String](Map())

  def addFieldDescription(name: String, fb: FieldDescription) = copy(fields = fields + (name -> fb))

  def field[F: Manifest](fieldFoo: T => F)(foo: FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F]) =
    fieldWithCustomName($[T](fieldFoo))(foo)

  def fieldOpt[F: Manifest](fieldFoo: T => Option[F])(foo: FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F]) =
    fieldWithCustomName($[T](fieldFoo))(foo)


  def extend[S >: T](foo: FormDescriptionBuilder[S] => FormDescriptionBuilder[S]): FormDescriptionBuilder[T] = {
    foo(this).asInstanceOf[FormDescriptionBuilder[T]]
  }

  def fieldWithCustomName[F: Manifest](fieldName: String)(foo: FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F]) = {

    val initialBuilder = FieldDescriptionBuilder[F](Map()).addProperty(FieldDescription.FieldType, implicitly[Manifest[F]].runtimeClass)

    addFieldDescription(fieldName, foo(initialBuilder).build)
  }


  def string(fieldFoo: T => String) =
    fieldBase[String](fieldFoo)(_.required) _

  def stringOpt(fieldFoo: T => Option[String]) =
    fieldBase[String](fieldFoo)(identity) _

  def string(name: String) =
    fieldWithCustomName[String](name) _

  def int(fieldFoo: T => Int) = fieldBase[Int](fieldFoo)(_.required) _

  def intOpt(fieldFoo: T => Option[Int]) = fieldBase[Int](fieldFoo)(identity) _

  def fieldBase[F: Manifest](fieldFoo: T => Any)
                            (foo: FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F])
                            (foo2: FieldDescriptionBuilder[F] => FieldDescriptionBuilder[F])
  = fieldWithCustomName[F]($[T](fieldFoo))(foo andThen foo2)

  def seq[A: Manifest](fieldFoo: T => Seq[A])(foo: FieldDescriptionBuilder[Seq[A]] => FieldDescriptionBuilder[Seq[A]])   = {
    val elementType = implicitly[Manifest[A]].runtimeClass

    fieldBase[Seq[A]](fieldFoo)(_.addProperty(FieldDescription.ListElementType, elementType).required)(foo)
  }

  def name(n: String) = copy(attrs = attrs + ("name" -> n))

  def build = FormDescription(fields = fields, attrs = attrs)

}


object FormBuilderDsl {
  def emptyFormBuilder[T: Manifest] = new FormDescriptionBuilder[T]()

  def form[T: Manifest](formFoo: FormDescriptionBuilder[T] => FormDescriptionBuilder[T]) = formFoo(emptyFormBuilder)
}

