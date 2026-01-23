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
import org.scalamock.util.MacroAdapter.Context
import org.scalamock.util.{MacroAdapter, MacroUtils}


//! TODO - get rid of this nasty two-stage construction when https://issues.scala-lang.org/browse/SI-5521 is fixed
class MockMaker[C <: Context](val ctx: C) {
  class MockMakerInner[T: ctx.WeakTypeTag](mockContext: ctx.Expr[MockContext], stub: Boolean, mockName: Option[ctx.Expr[String]]) {

    import ctx.universe._
    import Flag._
    import definitions._

    import scala.language.reflectiveCalls

    val utils = new MacroUtils[ctx.type](ctx)

    import utils._

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

    def isPathDependentThis(t: Type): Boolean = t match {
      case TypeRef(pre, _, _) => isPathDependentThis(pre)
      case ThisType(tpe) => tpe == typeToMock.typeSymbol
      case _ => false
    }

    /**
     * Checks if a type is a ThisType that refers to the type being mocked.
     */
    def isThisType(t: Type): Boolean = t match {
      case ThisType(tpe) => tpe == typeToMock.typeSymbol
      case _ => false
    }

    /**
      * Translates forwarder parameters into Trees.
      * Also maps Java repeated params into Scala repeated params.
      * Also handles path-dependent this types in type arguments.
      */
    def forwarderParamType(t: Type): Tree = t match {
      case TypeRef(pre, sym, args) if sym == JavaRepeatedParamClass =>
        TypeTree(internal.typeRef(pre, RepeatedParamClass, args))
      case TypeRef(_, sym, args) if isPathDependentThis(t) =>
        AppliedTypeTree(Ident(TypeName(sym.name.toString)), args.map(forwarderParamType))
      case TypeRef(_, sym, args) if args.exists(arg => isThisType(arg) || arg.exists(isThisType)) =>
        AppliedTypeTree(Ident(sym), args.map(forwarderParamType))
      case t if isThisType(t) =>
        SingletonTypeTree(This(TypeName("")))
      case _ =>
        TypeTree(t)
    }

    def methodsNotInObject =
      typeToMock.members filter (m => m.isMethod && !isMemberOfObject(m)) map (_.asMethod)

    //! TODO - This is a hack, but it's unclear what it should be instead. See
    //! https://groups.google.com/d/topic/scala-user/n11V6_zI5go/discussion
    def resolvedType(m: Symbol): Type =
      m.typeSignatureIn(internalSuperType(internalThisType(typeToMock.typeSymbol), typeToMock))

    def wrapByNameParam(p: Symbol): Tree = {
      val ident = Ident(TermName(p.name.toString))
      p.typeSignature match {
        case TypeRef(_, sym, _) if sym == definitions.ByNameParamClass =>
          q"() => $ident"
        case _ =>
          ident
      }
    }

    def forwarderImpl(m: MethodSymbol): ValOrDefDef = {
      val mt = resolvedType(m)
      val resType = forwarderParamType(finalResultType(mt))
      if (m.isStable) {
        ValDef(
          Modifiers(),
          TermName(m.name.toString),
          TypeTree(mt),
          castTo(literal(null), mt))
      } else {
        DefDef(
          Modifiers(Flag.OVERRIDE),
          m.name,
          mt.typeParams.map(internal.typeDef),
          paramss(mt).map(_.map { p =>
            ValDef(
              Modifiers(Flag.PARAM | (if (p.isImplicit) Flag.IMPLICIT else NoFlags)),
              TermName(p.name.toString),
              forwarderParamType(p.typeSignature),
              EmptyTree
            )
          }),
          forwarderParamType(finalResultType(mt)),
          q"""
            ${mockFunctionName(m)}
              .apply(..${paramss(mt).flatten.map(wrapByNameParam)})
              .asInstanceOf[$resType]
          """
        )
      }
    }

    def mockFunctionName(m: MethodSymbol) = {
      val index = typeToMock.member(m.name).asTerm.alternatives.indexOf(m)
      assert(index >= 0)
      TermName("mock$" + m.name + "$" + index)
    }

    // val <|mockname|> = new MockFunctionN[T1, T2, ..., R](mockContext, '<|name|>)
    def mockMethod(m: MethodSymbol): ValDef = {
      val mt = resolvedType(m)
      val clazz = classType(paramCount(mt), stub)
      val name = applyOn(scalaSymbol, "apply", mockNameGenerator.generateMockMethodName(m, mt))
      val termName = mockFunctionName(m)
      val additionalAnnotations = if(isScalaJs) List(jsExport(termName.encodedName.toString)) else Nil
      ValDef(
        Modifiers().mapAnnotations(additionalAnnotations ::: _),
        mockFunctionName(m),
        TypeTree(clazz),
        callConstructor(
          New(TypeTree(clazz)),
          mockContext.tree, name
        )
      )
    }

    // def <init>() = super.<init>()
    def initDef = {
      val primaryConstructorOpt = typeToMock.members.collectFirst {
        case method: MethodSymbolApi if method.isPrimaryConstructor => method
      }

      val constructorArgumentsTypes = primaryConstructorOpt.map { constructor =>
      val constructorTypeContext = constructor.typeSignatureIn(typeToMock)
      val constructorArguments = constructorTypeContext.paramLists
      constructorArguments.map {
          symbols => symbols.map(_.typeSignatureIn(constructorTypeContext))
        }
      }

      val tnEmpty = TypeName("") // typeNames.EMPTY
      val tnConstructor = TermName("<init>") // termNames.CONSTRUCTOR
      val superCall: Tree = Select(Super(This(tnEmpty), tnEmpty), tnConstructor)
      val constructorCall = constructorArgumentsTypes.fold(Apply(superCall, Nil).asInstanceOf[Tree]) { symbols =>
        symbols.foldLeft(superCall) {
          case (acc, symbol) => Apply(acc, symbol.map(tpe => q"null.asInstanceOf[$tpe]"))
        }
      }

      DefDef(
        Modifiers(),
        tnConstructor,
        List(),
        List(List()),
        TypeTree(),
        Block(
          List(constructorCall),
          Literal(Constant(()))))
    }

    // new <|typeToMock|> { <|members|> }
    def anonClass(members: List[Tree]) =
      Block(
        List(
          ClassDef(
            Modifiers(FINAL),
            anon,
            List(),
            Template(
              List(TypeTree(typeToMock)),
              noSelfType,
              initDef +: members)
            )
          ),
        callConstructor(New(Ident(anon))))

    /**
     * Class that is responsible for creating mock (and its methods) names so they can be reported on expectations error.
     * It either uses mock name specified by user or asks mockContext to generate new one.
     */
    class MockNameGenerator() {
      private val mockNameValName = TermName("mock$special$mockName")

      /** Member of mock that holds mock name */
      val mockNameVal = {
        val mockNameValRhs = {
          if (mockName.nonEmpty) {
            // new String(mockNameExpr)
            callConstructor(New(scalaString), mockName.get.tree)
          } else {
            // mockContext.generateMockDefaultName(prefix).name
            val namePrefix = Literal(Constant(if (stub) "stub" else "mock"))
            selectTerm(applyOn(mockContext.tree, "generateMockDefaultName", namePrefix), "name")
          }
        }
        // val mock$special$mockName = ...
        ValDef(Modifiers(), mockNameValName, TypeTree(), mockNameValRhs)
      }

      def generateMockMethodName(method: MethodSymbol, methodType: Type): Tree = {
        val mockType: Type = typeToMock.resultType
        val mockTypeNamePart: String = mockType.typeSymbol.name.toString
        val mockTypeArgsPart: String = generateTypeArgsString(mockType.typeArgs)
        val objectNamePart: Tree = Select(This(anon), mockNameValName)

        val methodTypeParamsPart: String = generateTypeArgsString(methodType.typeParams map (_.name))
        val methodNamePart: String = method.name.toString

        // "<%s> %s%s.%s".format(objectNamePart, mockTypeNamePart, mockTypeArgsPart, methodNamePart, methodTypeParamsPart)
        val formatStr = applyOn(scalaPredef, "augmentString", literal("<%s> %s%s.%s%s"))
        applyOn(formatStr, "format",
          objectNamePart, literal(mockTypeNamePart), literal(mockTypeArgsPart), literal(methodNamePart), literal(methodTypeParamsPart))
      }

      private def generateTypeArgsString(typeArgs: List[Any]): String = {
        if (typeArgs.nonEmpty)
          "[%s]".format(typeArgs.mkString(","))
        else ""
      }
    }

    val mockNameGenerator = new MockNameGenerator()
    val typeToMock = weakTypeOf[T]
    val anon = TypeName("$anon")
    val methodsToMock = methodsNotInObject.filter { m =>
      !m.isConstructor && !m.isPrivate && m.privateWithin == NoSymbol &&
      !m.isFinal &&
        !m.asInstanceOf[reflect.internal.HasFlags].hasFlag(reflect.internal.Flags.BRIDGE) &&
        !m.isParamWithDefault && // see issue #43
        !m.annotations.exists(_.tree.tpe =:= typeOf[scala.deprecatedOverriding]) &&
        (!(m.isStable || m.isAccessor) ||
          m.asInstanceOf[reflect.internal.HasFlags].isDeferred) //! TODO - stop using internal if/when this gets into the API
    }.toList
    val forwarders = methodsToMock map forwarderImpl
    val mocks = methodsToMock map mockMethod
    val members = mockNameGenerator.mockNameVal :: forwarders ++ mocks

    def make = {
      val result = castTo(anonClass(members), typeToMock)

      //        println("------------")
      //        println(showRaw(result))
      //        println("------------")
      //        println(show(result))
      //        println("------------")

      ctx.Expr(result)
    }
  }
}
