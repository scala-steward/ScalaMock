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

import org.scalamock.util.MacroAdapter.Context
import org.scalamock.util.{MacroAdapter, MacroUtils}
import org.scalamock.stubs.StubbedMethod
import org.scalamock.stubs.{CallLog, StubIO, Stub}

private[scalamock]
class StubMaker[C <: Context](val ctx: C) {
  class StubMakerInner[T: ctx.WeakTypeTag](
    createdStubs: ctx.Expr[CreatedStubs],
    stubUniqueIndexGenerator: ctx.Expr[StubUniqueIndexGenerator]
  ) {
    import ctx.universe._

    import scala.language.reflectiveCalls

    val utils = new MacroUtils[ctx.type](ctx)

    import utils._
    import definitions._

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
        TypeTree(internalTypeRef(pre, RepeatedParamClass, args))
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
      typeToMock.members
        .filter(m => m.isMethod && !isMemberOfObject(m))
        .map(_.asMethod)

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
      val mt = m.typeSignatureIn(
        internal.superType(internal.thisType(typeToMock.typeSymbol), typeToMock)
      )
      val resType = forwarderParamType(finalResultType(mt))

      if (m.isVal) {
        ValDef(
          Modifiers(Flag.OVERRIDE),
          m.name,
          resType,
          q"null.asInstanceOf[$resType]"
        )
      } else {
        // def <|name|>(p1: T1, p2: T2, ...): T = <|mockname|>(p1, p2, ...)
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
          resType,
          q"""
           ${mockFunctionName(m)}
             .impl(${tupledArgs(paramss(mt).flatten.map(wrapByNameParam))})
             .asInstanceOf[$resType]
          """
        )
      }
    }

    def mockFunctionName(m: MethodSymbol) = {
      val index = typeToMock.member(m.name).asTerm.alternatives.indexOf(m)
      assert(index >= 0)
      TermName("stub$" + m.name + "$" + index)
    }

    def tupledArgs(args: List[Tree]): Tree =
      args match {
        case Nil => Literal(Constant(()))
        case List(arg0) => arg0
        case List(arg0, arg1) => q"($arg0, $arg1)"
        case List(arg0, arg1, arg2) => q"($arg0, $arg1, $arg2)"
        case List(arg0, arg1, arg2, arg3) => q"($arg0, $arg1, $arg2, $arg3)"
        case List(arg0, arg1, arg2, arg3, arg4) => q"($arg0, $arg1, $arg2, $arg3, $arg4)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19, $arg20)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20, arg21) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19, $arg20, $arg21)"
        case _ => ctx.abort(ctx.enclosingPosition, "ScalaMock: Can't handle methods with more than 22 parameters (yet)")
      }

    def mockMethod(m: MethodSymbol): ValDef = {
      val finalRt = finalResultType(m.typeSignature)
      val termName = mockFunctionName(m)
      val additionalAnnotations = if(isScalaJs) List(jsExport(termName.encodedName.toString)) else Nil
      val summonedLog = ctx.inferImplicitValue(typeOf[CallLog], silent = true)
      val summonedIO = ctx.inferImplicitValue(typeOf[StubIO], silent = true)
      val summonedIOOpt = (if (summonedIO != EmptyTree) Some(summonedIO) else None)
        .filter { io =>
          finalRt <:< appliedType(io.tpe.member(TypeName("F")), List(typeOf[Any], typeOf[Any]))
        }
      ValDef(
        Modifiers().mapAnnotations(additionalAnnotations ::: _),
        termName,
        TypeTree(typeOf[StubbedMethod.Internal[Any, Any]]),
        callConstructor(
          New(TypeTree(typeOf[StubbedMethod.Internal[Any, Any]])),
          generateMockMethodName(m, m.typeSignature),
          if (summonedLog == EmptyTree) q"None" else q"Some($summonedLog)",
          summonedIOOpt.fold(q"None": Tree)(io => q"Some($io)"),
        )
      )
    }

    def clearMethod(methods: List[MethodSymbol]): DefDef = {
      val termName = TermName("stubs$macro$clear")
      val additionalAnnotations = if(isScalaJs) List(jsExport(termName.encodedName.toString)) else Nil

      DefDef(
        Modifiers().mapAnnotations(additionalAnnotations ::: _),
        termName,
        Nil,
        List(List()),
        TypeTree(typeOf[Unit]),
        Block(
          methods.map(mockFunctionName)
            .map { name => Apply(Select(Select(This(anon), name), TermName("clear")), Nil) },
          q"()"
        )
      )
    }

    def mockIdxVal = {
      val termName = TermName("stubs$macro$idx")
      ValDef(
        Modifiers(),
        termName,
        TypeTree(typeOf[Int]),
        q"$stubUniqueIndexGenerator.nextIdx()"
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

      val superCall: Tree = Select(Super(This(typeNames.EMPTY), typeNames.EMPTY), termNames.CONSTRUCTOR)
      val constructorCall = constructorArgumentsTypes
        .fold(Apply(superCall, Nil).asInstanceOf[Tree]) { symbols =>
        symbols.foldLeft(superCall) {
          case (acc, symbol) => Apply(acc, symbol.map(tpe => q"null.asInstanceOf[$tpe]"))
        }
      }

      DefDef(
        Modifiers(),
        termNames.CONSTRUCTOR,
        List(),
        List(List()),
        TypeTree(),
        Block(
          List(constructorCall),
          Literal(Constant(())))
      )
    }

    private def generateMockMethodName(method: MethodSymbol, methodType: Type): Tree = {
      val mockType: Type = typeToMock.resultType
      val mockTypeNamePart: String = mockType.typeSymbol.name.toString
      val mockTypeArgsPart: String = generateTypeArgsString(mockType.typeArgs)
      val idx: Tree = Select(This(anon), TermName("stubs$macro$idx"))

      val methodTypePart: String = methodType.toString
      val methodNamePart: String = method.name.toString

      // "<%s> %s%s.%s".format(objectNamePart, mockTypeNamePart, mockTypeArgsPart, methodNamePart, methodTypeParamsPart)
      val formatStr = applyOn(scalaPredef, "augmentString", literal("<stub-%s> %s%s.%s%s"))
      applyOn(formatStr, "format",
        idx, literal(mockTypeNamePart), literal(mockTypeArgsPart), literal(methodNamePart), literal(methodTypePart))
    }


    private def generateTypeArgsString(typeArgs: List[Any]): String = {
      if (typeArgs.nonEmpty)
        "[%s]".format(typeArgs.mkString(","))
      else ""
    }

    // new <|typeToMock|> { <|members|> }
    def anonClass(members: List[Tree]) = {
      val isTrait = typeToMock.typeSymbol.asInstanceOf[reflect.internal.HasFlags].isTrait
      Block(
        List(
          ClassDef(
            Modifiers(Flag.FINAL),
            anon,
            List(),
            Template(
              if (isTrait) List(TypeTree(typeOf[AnyRef]), TypeTree(typeToMock)) else List(TypeTree(typeToMock)),
              noSelfType,
              initDef +: members
            )
          )
        ),
        Apply(Select(New(Ident(anon)), termNames.CONSTRUCTOR), Nil)
      )
    }

    val typeToMock = weakTypeOf[T]
    val anon = TypeName("$anon")
    val methodsToMock = methodsNotInObject.filter { m =>
      val flags = m.asInstanceOf[reflect.internal.HasFlags]
      !m.isConstructor && !m.isPrivate && m.privateWithin == NoSymbol &&
      !m.isFinal &&
      !flags.hasFlag(reflect.internal.Flags.BRIDGE) &&
      !m.isParamWithDefault && // see issue #43
      !m.annotations.exists(_.tree.tpe =:= typeOf[scala.deprecatedOverriding]) &&
      (!(m.isStable || m.isAccessor) || flags.isDeferred)
    }.toList
    val forwarders = methodsToMock map forwarderImpl
    val mocks = methodsToMock.map(mockMethod(_))
    val members = clearMethod(methodsToMock) :: mockIdxVal :: (forwarders ++ mocks)

    def make: ctx.Expr[Stub[T]] = {
      val result = castTo(anonClass(members), typeToMock)

              //println("------------")
              //println(showRaw(result))
              //println("------------")
              //println(show(result))
              //println("------------")

      ctx.Expr[Stub[T]](q"$createdStubs.bind($result).asInstanceOf[_root_.org.scalamock.stubs.Stub[${weakTypeTag[T]}]]")
    }
  }
}
