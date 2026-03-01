// Copyright (c) 2011-2015 ScalaMock Contributors (https://github.com/paulbutcher/ScalaMock/graphs/contributors)
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

package org.scalamock.clazz

import org.scalamock.context.MockContext
import org.scalamock.function._
import org.scalamock.util.Utils

import scala.reflect.macros.blackbox

object MockMaker {
  def instance[T: ctx.WeakTypeTag](ctx: blackbox.Context)(
      mockContext: ctx.Expr[MockContext],
      stub: Boolean,
      mockName: Option[ctx.Expr[String]]
  ): ctx.Expr[T] = {
    import ctx.universe._
    val utils = new Utils[ctx.type](ctx)

    val typeToMock = weakTypeOf[T]
    val anon = TypeName("$anon")

    val mockNameValName = TermName("mock$special$mockName")
    val mockNameVal =
      ValDef(
        Modifiers(),
        mockNameValName,
        TypeTree(),
        mockName match {
          case Some(name) =>
            name.tree
          case None =>
            q"$mockContext.generateMockDefaultName(${if (stub) "stub" else "mock"}).name"
        }
      )

    def classType(paramCount: Int, stub: Boolean): Type = {
      if (stub) paramCount match {
        case 0 => typeOf[StubFunction0[Any]]
        case 1 => typeOf[StubFunction1[Any, Any]]
        case 2 => typeOf[StubFunction2[Any, Any, Any]]
        case 3 => typeOf[StubFunction3[Any, Any, Any, Any]]
        case 4 => typeOf[StubFunction4[Any, Any, Any, Any, Any]]
        case 5 => typeOf[StubFunction5[Any, Any, Any, Any, Any, Any]]
        case 6 => typeOf[StubFunction6[Any, Any, Any, Any, Any, Any, Any]]
        case 7 => typeOf[StubFunction7[Any, Any, Any, Any, Any, Any, Any, Any]]
        case 8 => typeOf[StubFunction8[Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 9 => typeOf[StubFunction9[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 10 => typeOf[StubFunction10[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 11 => typeOf[StubFunction11[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 12 => typeOf[StubFunction12[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 13 => typeOf[StubFunction13[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 14 => typeOf[StubFunction14[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 15 => typeOf[StubFunction15[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 16 => typeOf[StubFunction16[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 17 => typeOf[StubFunction17[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 18 => typeOf[StubFunction18[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 19 => typeOf[StubFunction19[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 20 => typeOf[StubFunction20[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 21 => typeOf[StubFunction21[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 22 => typeOf[StubFunction22[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case _ => ctx.abort(ctx.enclosingPosition, "ScalaMock: Can't handle methods with more than 22 parameters (yet)")
      } else paramCount match {
        case 0 => typeOf[MockFunction0[Any]]
        case 1 => typeOf[MockFunction1[Any, Any]]
        case 2 => typeOf[MockFunction2[Any, Any, Any]]
        case 3 => typeOf[MockFunction3[Any, Any, Any, Any]]
        case 4 => typeOf[MockFunction4[Any, Any, Any, Any, Any]]
        case 5 => typeOf[MockFunction5[Any, Any, Any, Any, Any, Any]]
        case 6 => typeOf[MockFunction6[Any, Any, Any, Any, Any, Any, Any]]
        case 7 => typeOf[MockFunction7[Any, Any, Any, Any, Any, Any, Any, Any]]
        case 8 => typeOf[MockFunction8[Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 9 => typeOf[MockFunction9[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 10 => typeOf[MockFunction10[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 11 => typeOf[MockFunction11[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 12 => typeOf[MockFunction12[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 13 => typeOf[MockFunction13[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 14 => typeOf[MockFunction14[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 15 => typeOf[MockFunction15[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 16 => typeOf[MockFunction16[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 17 => typeOf[MockFunction17[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 18 => typeOf[MockFunction18[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 19 => typeOf[MockFunction19[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 20 => typeOf[MockFunction20[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 21 => typeOf[MockFunction21[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case 22 => typeOf[MockFunction22[Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any, Any]]
        case _ =>
          ctx.abort(ctx.enclosingPosition, "ScalaMock: Can't handle methods with more than 22 parameters (yet)")
      }
    }

    // val <|mockname|> = new MockFunctionN[T1, T2, ..., R](mockContext, '<|name|>)
    def mockFunction(m: utils.MockableDefinition, mockNameValName: TermName): ValOrDefDef = {
      val clazz = classType(paramCount(m.tpe), stub)
      val mockType: Type = typeToMock.resultType
      val objectNamePart: Tree = Select(This(anon), mockNameValName)
      val typeArgs = Literal(Constant(
        if (mockType.typeArgs.nonEmpty) mockType.typeArgs.mkString("[", ",", "]") else ""
      ))
      val methodTypeArgs = Literal(Constant(
        if (m.tpe.typeParams.nonEmpty) m.tpe.typeParams.map(_.name).mkString("[", ",", "]") else ""
      ))
      val name = q"""
         scala.Symbol(
           scala.Predef
             .augmentString("<%s> %s%s.%s%s")
             .format(
               $objectNamePart,
               ${Literal(Constant(mockType.typeSymbol.name.toString))},
               $typeArgs,
               ${Literal(Constant(m.symbol.name.toString))},
               $methodTypeArgs
             )
           )
         """
      ValDef(
        Modifiers().mapAnnotations(m.annotations ::: _),
        m.name.asInstanceOf[TermName],
        TypeTree(clazz),
        Apply(Select(New(TypeTree(clazz)), termNames.CONSTRUCTOR), List(mockContext.tree, name))
      )
    }

    def paramCount(methodType: Type): Int = methodType match {
      case MethodType(params, result) => params.length + paramCount(result)
      case PolyType(_, result) => paramCount(result)
      case _ => 0
    }

    utils.instance[T] { defn =>
      mockNameVal :: defn.map(mockFunction(_, mockNameValName))
    }
  }
}
