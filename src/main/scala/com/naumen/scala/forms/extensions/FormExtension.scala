package com.naumen.scala.forms.extensions

import java.util.Date
import com.naumen.scala.forms.FieldDescriptionBuilder


object FieldCustomizers {

  import FieldExtensionsAttrs._

  implicit class StringFieldBuilderExtender(val fb: FieldDescriptionBuilder[String])
    {
    def minLength(length: Int) = fb.addProperty(MinLength, length)
    def maxLength(length: Int) = fb.addProperty(MaxLength, length)
  }

  implicit class SeqFieldBuilderExtender[A](val fb: FieldDescriptionBuilder[Seq[A]])
     {
  }
  implicit class DateFieldBuilderExtender(val fb: FieldDescriptionBuilder[Date])
  {
    def format(datePattern: String) = fb.addProperty(DateFormat, datePattern)
  }
}

trait FieldExtensionsAttrs {
  val DateFormat = "DateFormat"
  val MinLength = "MinLength"
  val MaxLength = "MaxLength"
}

object FieldExtensionsAttrs extends FieldExtensionsAttrs
