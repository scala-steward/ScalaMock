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

package org.scalamock.stubs

import java.util.concurrent.atomic.AtomicReference

/**
 * Representation of stubbed method without arguments.
 *
 * [[Stubs]] interface provides implicit conversion from selected method to StubbedMethod0.
 * {{{
 *   trait Foo:
 *     def foo0: Int
 *     def foo00(): String
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * Scala 2
 * {{{
 *   val foo0Stubbed: StubbedMethod0[Int] = (() => foo.foo0)
 *   val foo00Stubbed: StubbedMethod0[String] = (() => foo.foo00())
 * }}}
 *
 * Scala 3
 * {{{
 *   val foo0Stubbed: StubbedMethod0[Int] = foo.foo0
 *   val foo00Stubbed: StubbedMethod0[String] = foo.foo00()
 * }}}
 * */
trait StubbedMethod0[R] extends StubbedMethod.Order {
  type Result = R

  /** Allows to set result for method without arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.foo00().returns("abc")
   *   foo.foo00() // "abc"
   *  }}}
   *  Scala 2
   * {{{
   *    (() => foo.foo00()).returns("abc")
   *    foo.foo00() // "abc"
   * }}}
   * */
  def returns(f: => Result): Unit

  /** Allows to get number of times method was executed.
   *
   *  Scala 3
   *  {{{
   *   foo.foo0.returns(5)
   *   foo.foo0
   *   foo.foo0
   *
   *   foo.foo0.times // 2
   *  }}}
   *  Scala 2
   * {{{
   *    (() => foo.foo0).returns(5)
   *    foo.foo0
   *    foo.foo0
   *
   *    (() => foo.foo0).times // 2
   * }}}
   * */
  def times: Int
}

/**
 * Representation of stubbed method with arguments.
 *
 * [[Stubs]] interface provides implicit conversions from selected method to StubbedMethod.
 * {{{
 *   trait Foo:
 *     def foo(x: Int): Int
 *     def fooBar(bar: Boolean, baz: String): String
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * Scala 2
 * {{{
 *   val fooStubbed: StubbedMethod[Int, Int] = foo.foo _
 *   val fooBarStubbed: StubbedMethod[(Boolean, String), String] = foo.fooBar _
 * }}}
 *
 * Scala 3
 * {{{
 *   val fooStubbed: StubbedMethod[Int, Int] = foo.foo
 *   val fooBarStubbed: StubbedMethod[(Boolean, String), String] = foo.fooBar
 * }}}
 * */

trait StubbedMethod[A, R] extends StubbedMethod.Order {
  type Args = A
  type Result = R

  /** Allows to set result for method with arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.fooBar.returns:
   *     case (true, "bar") => "true"
   *     case _ => "false
   *  }}}
   *  Scala 2
   * {{{
   *   (foo.fooBar _).returns {
   *     case (true, "bar") => "true"
   *     case _ => "false"
   *   }
   * }}}
   *
   * */
  def returns(f: Args => Result): Unit

  /** Allows to get number of times method was executed.
   *
   *  Scala 3
   *  {{{
   *   foo.foo.returns(_ => 5)
   *   foo.foo(1)
   *
   *   foo.foo.times // 1
   *  }}}
   *  Scala 2
   * {{{
   *    (foo.foo _).returns(_ => 5)
   *    foo.foo(1)
   *
   *    (foo.foo _).times // 1
   * }}}
   * */
  def times: Int

  /** Allows to get number of times method was executed with specific arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.foo.returns(_ => 5)
   *   foo.foo(1)
   *
   *   foo.foo.times(1) // 1
   *   foo.foo.times(100) // 0
   *  }}}
   *  Scala 2
   * {{{
   *    (foo.foo _).returns(_ => 5)
   *    foo.foo(1)
   *
   *   (foo.foo _).times(1) // 1
   *   (foo.foo _).times(100) // 0
   * }}}
   * */
  final def times(args: Args): Int = calls.count(_ == args)

  /** Allows to get arguments with which method was executed.
   *
   *  Scala 3
   *  {{{
   *   foo.foo.returns(_ => 5)
   *   foo.foo(1)
   *   foo.foo(100)
   *
   *   foo.foo.calls // List(1, 100)
   *  }}}
   *  Scala 2
   * {{{
   *    (foo.foo _).returns(_ => 5)
   *    foo.foo(1)
   *    foo.foo(100)
   *
   *   (foo.foo _).calls // List(1, 100)
   * }}}
   * */
  def calls: List[Args]
}

object StubbedMethod {
  /** Allows to verify order of calls. */
  sealed trait Order {
    /**
     * Returns true if this method was called before other method.
     *
     *  Scala 3
     *  {{{
     *   foo.foo.returns(_ => 5)
     *   foo.fooBar.returns(_ => "bar")
     *   foo.foo(1)
     *   foo.fooBar(true, "bar")
     *
     *   foo.foo.isBefore(foo.fooBar) // true
     *  }}}
     *  Scala 2
     *  {{{
     *    (foo.foo _).returns(_ => 5)
     *    (foo.fooBar _).returns(_ => "bar")
     *    foo.foo(1)
     *    foo.fooBar(true, "bar")
     *
     *   (foo.foo _).isBefore(foo.fooBar _) // true
     *  }}}
     */
    def isBefore(other: Order)(implicit callLog: CallLog): Boolean

    /** Returns true if this method was called after other method.
     *
     *  Scala 3
     *  {{{
     *   foo.foo.returns(_ => 5)
     *   foo.fooBar.returns(_ => "bar")
     *   foo.foo(1)
     *   foo.fooBar(true, "bar")
     *
     *   foo.foo.isAfter(foo.fooBar) // false
     *  }}}
     *
     *  Scala 2
     *  {{{
     *    (foo.foo _).returns(_ => 5)
     *    (foo.fooBar _).returns(_ => "bar")
     *    foo.foo(1)
     *    foo.fooBar(true, "bar")
     *
     *   (foo.foo _).isAfter(foo.fooBar _) // false
     *  }}}
     * */
    def isAfter(other: Order)(implicit callLog: CallLog): Boolean

    /** Returns string representation of method.
     *  Representation currently depends on scala version.
     * */
    def asString: String
  }

  class Internal[A, R](
    override val asString: String,
    callLog: Option[CallLog],
    io: Option[StubIO]
  ) extends StubbedMethod[A, R]
      with StubbedMethod0[R] {
    override type Result = R
    override type Args = A

    override def toString = asString

    private val callsRef: AtomicReference[List[Args]] =
      new AtomicReference[List[Args]](Nil)

    private val resultRef: AtomicReference[Option[Args => Result]] =
      new AtomicReference[Option[Args => Result]](None)

    def impl(args: Args): Result =
      io match {
        case None =>
          callLog.foreach(_.internal.write(asString))
          callsRef.updateAndGet(args :: _)
          resultRef.get() match {
            case Some(f) => f(args)
            case None => throw new NotImplementedError()
          }
        case Some(io) =>
          io.flatMap(
            io.succeed {
              callLog.foreach(_.internal.write(asString))
              callsRef.updateAndGet(args :: _)
            }
          ) { _ =>
            resultRef.get() match {
              case Some(f) => f(args).asInstanceOf[io.F[Any, Any]]
              case None => io.die(new NotImplementedError())
            }
          }.asInstanceOf[Result]
      }

    def clear(): Unit = {
      callsRef.set(Nil)
      resultRef.set(None)
    }

    override def returns(f: Args => Result): Unit =
      resultRef.set(Some(f))

    override def returns(f: => Result): Unit =
      resultRef.set(Some(_ => f))

    override def times: Int =
      callsRef.get().length

    override def calls: List[Args] =
      callsRef.get().reverse

    override def isBefore(other: Order)(implicit callLog: CallLog): Boolean = {
      val actual = callLog.internal.calledMethods
      actual.indexOf(other.asString, actual.indexOf(asString)) != -1
    }

    override def isAfter(other: Order)(implicit callLog: CallLog): Boolean = {
      val actual = callLog.internal.calledMethods
      actual.indexOf(asString, actual.indexOf(other.asString)) != -1
    }
  }
}
