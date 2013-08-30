package com.naumen.scala.forms.extensions

import com.naumen.scala.forms.{FieldDescriptionBuilder, FormDescriptionBuilder}
import java.util.Date
import com.naumen.scala.utils.FieldNameGetter
import com.naumen.scala.forms.FormDescriptionBuilder
import com.naumen.scala.forms.FieldDescriptionBuilder
import java.text.SimpleDateFormat

object FormExtension extends FieldNameGetter {
  val defaultDateFormat = new SimpleDateFormat("dd.MM.yyyy").toPattern
  import FieldCustomizers._
  implicit class MyFormExtender[T: Manifest](val fb: FormDescriptionBuilder[T]) {
    def dateOpt(fieldFoo: T => Option[Date]) = fb.fieldBase[Date](fieldFoo)(_.format(defaultDateFormat))  _
    def date(fieldFoo: T => Date) = fb.fieldBase[Date](fieldFoo)(_.required.format(defaultDateFormat))  _
  }
}


object FieldCustomizers {

  import FieldAttributes._

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
    def format(datePattern: String) = fb.addProperty(DatePattern, datePattern)
  }
}

object FieldAttributes {
  val DatePattern = "DateFormat"
  val MinLength = "MinLength"
  val MaxLength = "MaxLength"
}
