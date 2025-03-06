package org.scalamock.stubs

import zio._

/**
 *  Same as [[StubbedMethod0]], but with additional ZIO methods.
 */
class StubbedZIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  def returnsZIO(f: => Result): UIO[Unit] = ZIO.succeed(returns(f))

  def timesZIO: UIO[Int] = ZIO.succeed(times)

  def returns(f: => Result): Unit = delegate.returns(f)

  def times: Int = delegate.times
  
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  def asString: String = delegate.asString

  override def toString: String = asString
}

/**
 *  Same as [[StubbedMethod]], but with additional ZIO methods.
 */
class StubbedZIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] {
  def returnsZIO(f: Args => Result): UIO[Unit] = ZIO.succeed(returns(f))

  def callsZIO: UIO[List[Args]] = ZIO.succeed(calls)

  def timesZIO: UIO[Int] = ZIO.succeed(times)

  def timesZIO(args: Args): UIO[Int] = ZIO.succeed(times(args))

  def returns(f: Args => Result): Unit = delegate.returns(f)

  def times: Int = delegate.times

  def calls: List[Args] = delegate.calls

  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  def asString: String = delegate.asString

  override def toString: String = asString
}