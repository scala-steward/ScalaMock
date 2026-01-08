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

package org.scalamock.clazz

import scala.annotation.{experimental, tailrec}
import scala.quoted.*

private[scalamock] class Utils(using val quotes: Quotes):
  def newApi = false

  import quotes.reflect.*

  case class StubWithMethod(stub: Term, method: MockableDefinition):
    def selectReflect[T: Type](name: MockableDefinition => String): Expr[T] =
      '{
        ${ stub.asExpr }
          .asInstanceOf[scala.reflect.Selectable]
          // https://github.com/lampepfl/dotty/issues/18612
          .selectDynamic(${ Expr(scala.reflect.NameTransformer.encode(name(method))) })
          .asInstanceOf[T]
      }

  object MockableDefinitions:
    private val objectMethods = TypeRepr.of[Object].typeSymbol.methodMembers.toSet

    @experimental
    def find(tpe: TypeRepr, name: String, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      MockableDefinitions(tpe)
        .filter { method =>
          // should have same name
          method.symbol.name == name &&
          // should have same parameter count
          paramTypes.lengthCompare(method.rawTypes) == 0 &&
          // only poly should have appliedTypes
          (method.tpe match {
            case poly: PolyType => poly.paramTypes.lengthCompare(appliedTypes.length) == 0
            case _ => appliedTypes.isEmpty
          }) &&
            // params with filled ParamRef and few fixes should match
            paramTypes.zip(method.rawTypes.map(adjustTpe(_, method.tpe, appliedTypes))).forall(_ <:< _)
        }
        .sortWith((a, b) => a.rawTypes.zip(b.rawTypes).forall(_ <:< _))
        .headOption
        .getOrElse(report.errorAndAbort(s"Method with such signature not found"))

    private def adjustTpe(tpe: TypeRepr, methodTpe: TypeRepr, appliedTypes: List[TypeRepr]): TypeRepr =
      def updateParamRefs(tpe: TypeRepr, methodTpe: PolyType): TypeRepr =
        def replace(ref: ParamRef): TypeRepr =
          if appliedTypes.length > ref.paramNum then appliedTypes(ref.paramNum)
          else if methodTpe.paramBounds.length > ref.paramNum then methodTpe.paramBounds(ref.paramNum).hi
          else TypeRepr.of[Any]

        tpe match {
          case TypeBounds(lo, hi) =>
            TypeBounds(updateParamRefs(lo, methodTpe), updateParamRefs(hi, methodTpe))
            
          case p: ParamRef =>
            replace(p)

          case AppliedType(tycon: ParamRef, args) =>
            replace(tycon).appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case TypeRef(p: ParamRef, name) =>
            replace(p).typeSymbol.typeMember(name).typeRef

          case AppliedType(TypeRef(tycon: ParamRef, name), args) =>
            replace(tycon).typeSymbol.typeMember(name).typeRef.appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case AppliedType(tycon, args) =>
            tycon.appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case AndType(left, right) =>
            AndType(updateParamRefs(left, methodTpe), updateParamRefs(right, methodTpe))

          case OrType(left, right) =>
            OrType(updateParamRefs(left, methodTpe), updateParamRefs(right, methodTpe))

          case _ =>
            tpe
        }
      end updateParamRefs

      val preprocessed = tpe match
        case ByNameType(tpe) =>
          tpe
        case AppliedType(TypeRef(_, "<repeated>"), elemTyps) =>
          TypeRepr.typeConstructorOf(classOf[Seq[?]]).appliedTo(elemTyps)
        case tpe =>
          tpe

      methodTpe match
        case poly: PolyType =>
          updateParamRefs(preprocessed, poly)
        case _ =>
          preprocessed
    end adjustTpe

    def apply(tpe: TypeRepr): List[MockableDefinition] =
      val methods = tpe.typeSymbol.methodMembers
        .filter(sym =>
          !objectMethods.contains(sym) &&
            !sym.flags.is(Flags.Private) &&
            !sym.flags.is(Flags.Final) &&
            !sym.flags.is(Flags.Mutable) &&
            !sym.flags.is(Flags.Artifact) &&
            sym.privateWithin.isEmpty &&
            !sym.name.contains("$default$") &&
            !sym.hasAnnotation(TypeRepr.of[scala.deprecatedOverriding].typeSymbol)
        )
        .zipWithIndex
        .map((sym, idx) => MockableDefinition(idx, sym, tpe))

      val vals = tpe.typeSymbol.fieldMembers
        .filter(_.flags.is(Flags.Deferred))
        .map(sym => MockableDefinition(0, sym, tpe))
      methods ++ vals

  extension (tpe: TypeRepr)
    def collectTypes: (List[TypeRepr], TypeRepr) =
      @tailrec
      def loop(currentTpe: TypeRepr, argTypesAcc: List[List[TypeRepr]], resType: TypeRepr): (List[TypeRepr], TypeRepr) =
        currentTpe match
          case PolyType(_, _, res)          => loop(res, List.empty[TypeRepr] :: argTypesAcc, resType)
          case MethodType(_, argTypes, res) => loop(res, argTypes :: argTypesAcc, resType)
          case other                        => (argTypesAcc.reverse.flatten, other)
      loop(tpe, Nil, TypeRepr.of[Nothing])

  case class MockableDefinition(idx: Int, symbol: Symbol, ownerTpe: TypeRepr):
    val mockValName = s"mock$$${symbol.name}$$$idx"
    val stubValName = s"stub$$${symbol.name}$$$idx"
    val tpe = ownerTpe.memberType(symbol)
    val (rawTypes, rawResType) = tpe.widen.collectTypes

    @experimental
    def prepareResType(classSymbol: Symbol, methodArgs: List[List[Tree]], debug: Boolean = false): TypeRepr = {
      val methodTpe = This(classSymbol).tpe.memberType(symbol)
      val resType = methodTpe.collectTypes._2 match
        case ByNameType(tpe) => tpe
        case other => other

      methodTpe match
        case baseBindings: PolyType =>
          def loop(typeRepr: TypeRepr): TypeRepr =
            typeRepr match
              case pr@ParamRef(bindings, idx) if bindings == baseBindings =>
                methodArgs.head(idx).asInstanceOf[TypeTree].tpe

              case AndType(left, right) =>
                AndType(loop(left), loop(right))

              case OrType(left, right) =>
                OrType(loop(left), loop(right))

              case AppliedType(tycon, args) =>
                AppliedType(loop(tycon), args.map(loop))

              case AnnotatedType(other, annot) =>
                AnnotatedType(loop(other), annot)

              case ff@TypeRef(ref@ParamRef(bindings, idx), name) =>
                def getIndex(bindings: TypeRepr): Int =
                  @tailrec
                  def loop(bindings: TypeRepr, idx: Int): Int =
                    bindings match
                      case MethodType(_, _, method: MethodType) => loop(method, idx + 1)
                      case _ => idx

                  loop(bindings, 1)

                val maxIndex = methodArgs.length
                val parameterListIdx = maxIndex - getIndex(bindings)

                TypeSelect(methodArgs(parameterListIdx)(idx).asInstanceOf[Term], name).tpe

              case other => other

          loop(resType)

        case baseBindings: MethodType =>
          resType match
            case pr@ParamRef(bindings, idx) if bindings == baseBindings =>
              methodArgs.head(idx).asInstanceOf[TypeTree].tpe
            case other =>
              other

        case _ =>
          resType
    }
