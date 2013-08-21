package com.naumen.scala.forms


import java.lang.reflect.{Constructor, Method}
import javassist.util.proxy.{MethodFilter, ProxyFactory, MethodHandler, Proxy}

object FieldNameGetter extends FieldNameGetter

trait FieldNameGetter {

  def $[T](foo: T => Any)(implicit mf: Manifest[T]) = firstInvoked[T](foo)


  def firstInvoked[T](foo: T => Any)(implicit mf: Manifest[T]) = {

    var firstInvoked: String = null
    val mh = new MethodHandler {
      def invoke(p1: Any, p2: Method, p3: Method, p4: Array[AnyRef]) = {
        if (firstInvoked == null)
          firstInvoked = p2.getName
        p3.invoke(p1, p4: _*)
      }
    }

    foo(createProxyWithHandler[T](mh))
    firstInvoked
  }

  def createProxyWithHandler[T](handler: MethodHandler)(implicit mf: Manifest[T]): T with Proxy = {
    val proxy = createProxy[T]
    proxy.setHandler(handler)
    proxy
  }

  def createProxy[T](implicit mf: Manifest[T]): T with Proxy = {

    val clazz = mf.runtimeClass
    val proxyFactory = new ProxyFactory
    proxyFactory.setSuperclass(clazz)
    proxyFactory.setFilter(new MethodFilter {
      def isHandled(p1: Method) = true
    })
    val newClass = proxyFactory.createClass()
    val constructor = defaultConstructor(newClass)
    val parameters = defaultConstructorParameters(constructor)
    constructor.newInstance(parameters.toSeq: _*).asInstanceOf[T with Proxy]
  }

  def defaultConstructor(c: Class[_]) = c.getConstructors.head

  def defaultConstructorParameters(constructor: Constructor[_]) = {
    val parameterTypes = constructor.getParameterTypes
    val OptionClazz = classOf[Option[_]]
    parameterTypes.map {
      case java.lang.Boolean.TYPE => java.lang.Boolean.FALSE
      case Integer.TYPE => Integer.valueOf(0)
      case java.lang.Long.TYPE => java.lang.Long.valueOf(0)
      case java.lang.Double.TYPE => java.lang.Double.valueOf(0)
      case OptionClazz => None
      case _ => null
    }.map(_.asInstanceOf[AnyRef])
  }

}


