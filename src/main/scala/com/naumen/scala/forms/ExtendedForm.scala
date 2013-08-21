package com.naumen.scala.forms

import scala._
import play.api.data.{FormError, Form, Field, Mapping}


class ExtendedForm[T](fields: Map[String, MappingFieldBuilder[_]])(implicit private val mf: Manifest[T])
  extends Form[T](
    FormMapping[T](fields.map {
      case (name, mfb) => name -> mfb.asInstanceOf[Mapping[Any]]
    }.toMap),
    Map.empty[String, String],
    Seq.empty[FormError],
    None) {


  override def apply(key: String): Field = new ExtendedField(this, Field(
    this,
    key,
    constraints.get(key).getOrElse(Nil),
    formats.get(key),
    errors.collect {
      case e if e.key == key => e
    },
    data.get(key)), new FieldExtension(fields(key).propertyMap))

}

case class FieldExtension(attrs: Map[String, Any])


class ExtendedField(form: Form[_], field: Field, val ext: FieldExtension)
  extends Field(form, field.name, field.constraints, field.format, field.errors, field.value) {

}


