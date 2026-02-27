package org.scalamock.stubs.internal

import org.scalamock.stubs.StubbedMethod

import scala.reflect.macros.blackbox

private[scalamock]
object StubbedMethodFinderImpl {
  type Context = blackbox.Context

  def find[
    M: c.WeakTypeTag
  ](
    c: Context
  )(
    obj: c.Tree, 
    name: c.Name, 
    targs: List[c.Type], 
    actuals: List[c.universe.Type]
  ): c.Expr[M] = {
    import c.universe._

    def mockFunctionName(name: Name, t: Type, targs: List[Type]) = {
      val method = t.member(name).asTerm
      if (method.isOverloaded)
        "stub$" + name + "$" + method.alternatives.indexOf(StubbedMethodFinder.resolveOverloaded(c)(method, targs, actuals))
      else
        "stub$" + name + "$0"
    }

    val fullName = TermName(mockFunctionName(name, obj.tpe, targs))


    val code = c.Expr[M](
      q"""{
      import scala.scalajs.js
      $obj.asInstanceOf[js.Dynamic].$fullName.asInstanceOf[${weakTypeOf[M]}]
    }""")
    code
  }
}
