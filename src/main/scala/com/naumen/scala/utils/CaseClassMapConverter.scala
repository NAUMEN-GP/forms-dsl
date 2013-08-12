package com.naumen.scala.utils


import reflect.ClassTag
import com.thoughtworks.paranamer.{BytecodeReadingParanamer, CachingParanamer}

/**
 * Утилита для преобразования классов в Map и обратно
 */


object ClassConverter extends CaseClassMapConverter
trait CaseClassMapConverter {

  private val pn = new CachingParanamer(new BytecodeReadingParanamer)

  /**
   * Метод преобразует Map в Class[T]
   * @param m Map содержаший параметры класса и их значения
   * @param mf ClassTag
   * @tparam T Тип объекта
   * @return Экземпляр класса типа T
   */
  def toInstanceOf[T](m: Map[String, Any])(implicit mf: ClassTag[T]) = {
    val clazz = mf.runtimeClass
    val constructor = clazz.getDeclaredConstructors.head
    val parameters = pn.lookupParameterNames(constructor)
    val parameterTypes = constructor.getParameterTypes
    val arguments = parameters.zip(parameterTypes).map {
      case (p, pt) => prepareArgument(p, pt)(m)
    }
    constructor.newInstance(arguments: _*).asInstanceOf[T]
  }

  private def prepareArgument(name: String, t: Class[_])(m: Map[String, Any]): AnyRef = {
    //TODO NAME PROPERLY

    (m.get(name).getOrElse(null) match {
      case null => typesToDefaultValues(t)
      case x => x
    }).asInstanceOf[AnyRef]
  }

  /**
   * Метод преобразует Class в Map
   * @param cc экземпляр класса
   * @return Map содержаший параметры класса и их значения
   */
  def toMap(cc: AnyRef) = (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
    (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
  }

  def typesToDefaultValues: Class[_] => Any = {
    //TODO remove doubling
    case java.lang.Boolean.TYPE => java.lang.Boolean.FALSE
    case Integer.TYPE => Integer.valueOf(0)
    case java.lang.Long.TYPE => java.lang.Long.valueOf(0)
    case java.lang.Double.TYPE => java.lang.Double.valueOf(0)
    case _ => null
  }

}

