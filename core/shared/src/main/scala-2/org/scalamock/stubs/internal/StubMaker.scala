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

package org.scalamock.stubs.internal

import org.scalamock.util.Utils
import org.scalamock.stubs.StubbedMethod
import org.scalamock.stubs.{CallLog, Stub, StubIO}

import scala.reflect.macros.blackbox

object StubMaker {
  def instance[T: ctx.WeakTypeTag](ctx: blackbox.Context)(
    createdStubs: ctx.Expr[CreatedStubs],
    stubUniqueIndexGenerator: ctx.Expr[StubUniqueIndexGenerator]
  ): ctx.Expr[T] = {
    import ctx.universe._
    val utils = new Utils[ctx.type](ctx, newApi = true)

    val typeToMock = weakTypeOf[T]
    val anon = TypeName("$anon")

    def stubbedMethod(m: utils.MockableDefinition): ValDef = {
      val termName = m.name.asInstanceOf[TermName]
      val summonedLog = ctx.inferImplicitValue(typeOf[CallLog], silent = true)
      val summonedIO = ctx.inferImplicitValue(typeOf[StubIO], silent = true)
      val finalResType = utils.finalResultType(m.tpe)
      val summonedIOOpt = (if (summonedIO != EmptyTree) Some(summonedIO) else None)
        .filter { io =>
          finalResType <:< appliedType(io.tpe.member(TypeName("F")), List(typeOf[Any], typeOf[Any]))
        }
      ValDef(
        Modifiers().mapAnnotations(m.annotations ::: _),
        termName,
        TypeTree(typeOf[StubbedMethod.Internal[Any, Any]]),
        Apply(
          Select(New(TypeTree(typeOf[StubbedMethod.Internal[Any, Any]])), termNames.CONSTRUCTOR),
          List(
            generateMockMethodName(m),
            if (summonedLog == EmptyTree) q"None" else q"Some($summonedLog)",
            summonedIOOpt.fold(q"None": Tree)(io => q"Some($io)")
          )
        )
      )
    }

    def clearMethod(methods: List[utils.MockableDefinition]): DefDef = {
      val termName = TermName("stubs$macro$clear")
      DefDef(
        Modifiers().mapAnnotations(utils.scalaJSAnnotations(termName) ::: _),
        termName,
        Nil,
        List(List()),
        TypeTree(typeOf[Unit]),
        Block(
          methods.map(_.name.asInstanceOf[TermName])
            .map { name => Apply(Select(Select(This(anon), name), TermName("clear")), Nil) },
          q"()"
        )
      )
    }

    def mockIdxVal: ValDef = {
      val termName = TermName("stubs$macro$idx")
      ValDef(
        Modifiers(),
        termName,
        TypeTree(typeOf[Int]),
        q"$stubUniqueIndexGenerator.nextIdx()"
      )
    }

    def generateMockMethodName(m: utils.MockableDefinition): Tree = {
      val mockType: Type = typeToMock.resultType
      val mockTypeNamePart: String = mockType.typeSymbol.name.toString
      val mockTypeArgsPart: String =
        if (mockType.typeArgs.nonEmpty) mockType.typeArgs.mkString("[", ",", "]") else ""
      val idx: Tree = Select(This(anon), TermName("stubs$macro$idx"))

      val methodTypePart: String = m.tpe.toString
      val methodNamePart: String = m.symbol.name.toString

      q"""
        scala.Predef
          .augmentString("<%s> %s%s.%s%s")
          .format(
            "stub-" + $idx,
            ${Literal(Constant(mockTypeNamePart))},
            ${Literal(Constant(mockTypeArgsPart))},
            ${Literal(Constant(methodNamePart))},
            ${Literal(Constant(methodTypePart))}
          )
      """
    }

    ctx.Expr[Stub[T]](
      q"""$createdStubs
            .bind(${
              utils.instance[T] { defn =>
                clearMethod(defn) :: mockIdxVal :: defn.map(m => stubbedMethod(m))
              }
            })
            .asInstanceOf[_root_.org.scalamock.stubs.Stub[${weakTypeTag[T]}]]
       """
    )
  }
}
