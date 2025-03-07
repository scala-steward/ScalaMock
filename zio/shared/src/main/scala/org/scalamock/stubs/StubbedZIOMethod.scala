package org.scalamock.stubs

import zio._

/**
 * Representation of stubbed method without arguments.
 *
 * [[ZIOStubs]] interface provides implicit conversion from selected method to StubbedMethodZIO0.
 * {{{
 *   trait Foo:
 *     def foo00(): String
 *     def fooIO: IO[String, Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * The default way of getting stub for such method - convert it to a function () => R.
 *
 * Exclusively for ZIO - you can omit converting it to function
 *
 * {{{
 *   val foo00Stubbed: StubbedMethod0[String] = () => foo.foo00()
 *   val fooIOStubbed: StubbedMethod0[IO[String, Int]] = foo.fooIO
 * }}}
 * */
class StubbedZIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  /** Allows to set result for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).returnsZIO("result")
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *    } yield ()
   * }}}
   * */
  def returnsZIO(f: => R): UIO[Unit] = ZIO.succeed(returns(f))

  /** Allows to get number of times method was executed. Returns ZIO
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *      fooIOTimes <- foo.fooIO.timesZIO
   *    } yield fooIOTimes == 11 // true
   * }}}
   * */
  def timesZIO: UIO[Int] = ZIO.succeed(times)

  /** Allows to set result for method without arguments.
   *
   * {{{
   *   (() => foo.foo00()).returns("result")
   *   foo.fooIO.returns(ZIO.succeed(1))
   * }}}
   * */
  def returns(f: => R): Unit = delegate.returns(f)

  /** Allows to get number of times method was executed.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *    } yield foo.fooIO.times == 11 // true
   * }}}
   * */
  def times: Int = delegate.times

  /**
   * Returns true if this method was called before other method.
   */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  /**
   * Returns true if this method was called after other method.
   */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /**
   *  Returns string representation of method.
   *  Representation currently depends on scala version.
   * */
  def asString: String = delegate.asString

  override def toString: String = asString
}

/**
 * Representation of stubbed method with arguments.
 *
 * [[ZIOStubs]] interface provides implicit conversion from selected method to StubbedMethodZIO.
 * {{{
 *   trait Foo:
 *     def foo(x: Int): UIO[String]
 *     def bar(x: Int, y: String): IO[String, Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * Scala 3
 * {{{
 *   val fooStubbed: StubbedMethod[Int, UIO[String]] = foo.foo
 *   val barStubbed: StubbedMethod[(Int, String), IO[String, Int]] = foo.bar
 * }}}
 *
 * Scala 2
 * {{{
 *   val fooStubbed: StubbedMethod[Int, UIO[String]] = foo.foo _
 *   val barStubbed: StubbedMethod[(Int, String), IO[String, Int]] = foo.bar _
 * }}}
 * */
class StubbedZIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] {

  /** Allows to set result for method with arguments. Returns ZIO
   * 
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.returnsZIO((x, y) => ZIO.succeed(1))
   *   yield ()
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO((x, y) => ZIO.succeed(1))
   *   } yield ()
   *  }}}
   * */
  def returnsZIO(f: A => R): UIO[Unit] = ZIO.succeed(returns(f))

  /** Allows to get arguments with which method was executed. Returns ZIO
   * 
   *  Returns multiple arguments as tuple. One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for {
   *     _ <- foo.bar.returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- foo.bar.callsZIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- (foo.bar _).callsZIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   * */
  def callsZIO: UIO[List[A]] = ZIO.succeed(calls)

  /** Allows to get number of times method was executed. Returns ZIO
   *
   *  Scala 3
   * {{{
   *    for
   *      _ <- foo.bar.returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- foo.bar.timesZIO
   *    yield barTimes == 11 // true
   * }}}
   *  Scala 2
   *  {{{
   *    for {
   *      _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- (foo.bar _).timesZIO
   *    } yield barTimes == 11 // true
   * }}}
   * */
  def timesZIO: UIO[Int] = ZIO.succeed(times)

  /** Allows to get number of times method was executed with specific arguments. Returns ZIO
   *
   *  Scala 3
   *  {{{
   *    for
   *      _ <- foo.bar.returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- foo.bar.timesZIO((1, "foo"))
   *    yield barTimes == 11 // true
   * }}}
   * 
   *  Scala 2
   *  {{{
   *    for {
   *      _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- (foo.bar _).timesZIO((1, "foo"))
   *    } yield barTimes == 11 // true
   * }}}
   * */
  def timesZIO(args: A): UIO[Int] = ZIO.succeed(times(args))

  /** Allows to set result for method with arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.bar.returns((x, y) => ZIO.succeed(1))
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   (foo.bar _).returns((x, y) => ZIO.succeed(1))
   *  }}}
   * */
  def returns(f: A => R): Unit = delegate.returns(f)

  /** Allows to get number of times method was executed.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *    } yield foo.fooIO.times == 11 // true
   * }}}
   * */
  def times: Int = delegate.times

  /** Allows to get arguments with which method was executed.
   *  Returns multiple arguments as tuple.
   *  One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for {
   *     _ <- foo.bar.returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *   } yield foo.bar.calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *   } yield (foo.bar _).calls == List((1, "foo"), (2, "bar")) // true
   *   }}}
   * */
  def calls: List[A] = delegate.calls

  /**
   * Returns true if this method was called before other method.
   */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  /**
   * Returns true if this method was called after other method.
   */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /**
   *  Returns string representation of method.
   *  Representation currently depends on scala version.
   * */
  def asString: String = delegate.asString

  override def toString: String = asString
}