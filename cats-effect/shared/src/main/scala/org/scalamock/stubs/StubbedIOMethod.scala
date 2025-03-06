package org.scalamock.stubs

import cats.effect.IO

/**
 *  Same as [[StubbedMethod0]], but with additional IO methods.
 */
class StubbedIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  def returnsIO(f: => R): IO[Unit] = IO(returns(f))

  def timesIO: IO[Int] = IO(times)

  def returns(f: => R): Unit = delegate.returns(f)

  def times: Int = delegate.times

  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  def asString: String = delegate.asString

  override def toString: String = asString
}

/**
 *  Same as [[StubbedMethod]], but with additional IO methods.
 */
class StubbedIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] {
  def returnsIO(f: Args => Result): IO[Unit] = IO(returns(f))

  def callsIO: IO[List[Args]] = IO(calls)

  def timesIO: IO[Int] = IO(times)

  def timesIO(args: Args): IO[Int] = IO(times(args))

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