package org.scalamock.stubs

import zio._

/**
 *  Same as [[StubbedMethod0]], but with additional ZIO methods.
 */
class StubbedZIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  def returnsZIO(f: => R): UIO[Unit] = ZIO.succeed(returns(f))

  def timesZIO: UIO[Int] = ZIO.succeed(times)

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
 *  Same as [[StubbedMethod]], but with additional ZIO methods.
 */
class StubbedZIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] {
  def returnsZIO(f: A => R): UIO[Unit] = ZIO.succeed(returns(f))

  def callsZIO: UIO[List[A]] = ZIO.succeed(calls)

  def timesZIO: UIO[Int] = ZIO.succeed(times)

  def timesZIO(args: A): UIO[Int] = ZIO.succeed(times(args))

  def returns(f: A => R): Unit = delegate.returns(f)

  def times: Int = delegate.times

  def calls: List[A] = delegate.calls

  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  def asString: String = delegate.asString

  override def toString: String = asString
}