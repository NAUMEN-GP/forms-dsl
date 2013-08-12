package com.naumen.scala.utils.tests

import org.specs2.mutable.Specification
import com.naumen.scala.utils.CaseClassMapConverter

class ClassConverterHelperTest extends Specification with CaseClassMapConverter {

  "ClassConverterHelper" should {
    "conertObjectToMap" in {
      val map = toMap(new ClassConverterHelperTestSampleClass)
      map.get("foo") === None
      map("i") === 42
      map("value") === true
      map("optional") === Some("x")
    }

    "convertMapToInstance" in {
      val map = Map(
        "i" -> (13: Int),
        "value" -> (true: Boolean),
        "optional" -> Some("y")
      )

      val instance = toInstanceOf[ClassConverterHelperTestSampleClass](map)

      instance.i === 13
      instance.value === true
      instance.optional === Some("y")
    }
  }
}

case class ClassConverterHelperTestSampleClass(
                                                value: Boolean = true,
                                                optional: Option[String] = Some("x"),
                                                i: Int = 42)


