package com.naumen.scala.utils.tests

import org.specs2.mutable._
import com.naumen.scala.utils.FieldNameGetter

class FieldNameGetterTest extends Specification with FieldNameGetter{
  "FieldNameGetter" should {
    "get Name Of The First Invoked Member" in {
      val nameOf = $[FieldNameGetterTestSampleClass] _

      nameOf(_.field) === "field"
      nameOf(_.variable) === "variable"
      nameOf(_.foo) === "foo"
    }
  }
}


private case class FieldNameGetterTestSampleClass(field: String) {
  var variable = 2
  def foo = "3"
}
