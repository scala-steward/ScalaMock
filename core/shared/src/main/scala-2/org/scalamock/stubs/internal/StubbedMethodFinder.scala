// Copyright (c) 2011-2025 ScalaMock Contributors (https://github.com/ScalaMock/ScalaMock/graphs/contributors)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.


package org.scalamock.stubs.internal

import org.scalamock.util.Utils
import org.scalamock.stubs.{StubbedMethod, Stub}

import scala.reflect.macros.blackbox

private[scalamock]
object StubbedMethodFinder {
  type Context = blackbox.Context

  /**
   * Given something of the structure <|o.m _|> where o is a mock object
   * and m is a method, find the corresponding MockFunction instance
   */
  def find[
    M: c.WeakTypeTag
  ](
    c: Context
  )(
    f: c.Expr[Any],
    actuals: List[c.universe.Type]
  ): c.Expr[M] = {
    import c.universe._
    val utils = new Utils[c.type](c)
    import utils._

    def mockedFunctionGetter(obj: Tree, name: Name, targs: List[Type]): c.Expr[M] =
      StubbedMethodFinderImpl.find[M](c)(obj, name, targs, actuals)

    def transcribeTree(tree: Tree, targs: List[Type] = Nil): c.Expr[M] = {
      tree match {
        case Select(qualifier, name) =>
           if (!(qualifier.tpe <:< typeOf[Stub[Any]]))
             reportError(s"This syntax is only available on Stub[T]")
           mockedFunctionGetter(qualifier, name, targs)
        case Block(stats, expr) => c.Expr[M](Block(stats, transcribeTree(expr).tree)) // see issue #62
        case Typed(expr, tpt) => transcribeTree(expr)
        case Function(vparams, body) => transcribeTree(body)
        case Apply(fun, args) => transcribeTree(fun)
        case TypeApply(fun, args) => transcribeTree(fun, args.map(_.tpe))
        case Ident(fun) => reportError(s"please declare '$fun' as MockFunctionx or StubFunctionx (e.g val $fun: MockFunction1[X, R] = ... if it has 1 parameter)")
        case _ => 
          reportError(
            s"""
              This syntax is only available on Stub[T].
              Current structure is not recognised: ${show(tree)}
              If you think this is a bug - please open a ticket at https://github.com/ScalaMock/ScalaMock/issues"
            """
          )
      }
    }

    transcribeTree(f.tree)
  }

  def resolveOverloaded(c: Context)(method: c.universe.TermSymbol, targs: List[c.universe.Type], actuals: List[c.universe.Type]): c.universe.Symbol = {
    val utils = new Utils[c.type](c)
    import c.universe._
    import utils._

    def sameTypes(types1: List[Type], types2: List[Type]) = {
      // see issue #34
      var these = types1.map(_.dealias)
      var those = types2.map(_.dealias)
      while (!these.isEmpty && !those.isEmpty && these.head =:= those.head) {
        these = these.tail
        those = those.tail
      }
      these.isEmpty && those.isEmpty
    }

    method.alternatives find { m =>
      val tpe = m.typeSignature
      val pts = {
        if (targs.nonEmpty && tpe.typeParams.length == targs.length)
          paramTypes(appliedType(tpe, targs))
        else
          paramTypes(tpe)
      }
      sameTypes(pts, actuals)
    } getOrElse {
      reportError(s"Unable to resolve overloaded method ${method.name}")
    }
  }
}
