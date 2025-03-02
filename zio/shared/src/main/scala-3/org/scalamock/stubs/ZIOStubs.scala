package org.scalamock.stubs

import org.scalamock.stubs.{StubbedZIOMethod, StubbedZIOMethod0}

import scala.language.implicitConversions
import zio.*

import scala.util.NotGiven

trait ZIOStubs extends StubsBase {
  private[scalamock] class ZIOStubIO extends StubIO {
    type F[+A, +B] = IO[A, B]

    def die(ex: Throwable): F[Nothing, Nothing] = ZIO.die(ex)
    def succeed[T](t: => T): F[Nothing, T] = ZIO.succeed(t)
    def flatMap[E, EE >: E, T, T2](fa: IO[E, T])(f: T => IO[EE, T2]) = fa.flatMap(f)
  }
  final given ZIOStubIO = ZIOStubIO()

  implicit inline def stubbed[R](inline f: => R)(using NotGiven[StubbedZIOMethod[?, ?]]): StubbedZIOMethod0[R] =
    StubbedZIOMethod0[R](stubbed00Impl[R](f))

  implicit inline def stubbed[T1, R](inline f: () => R): StubbedZIOMethod0[R] =
    StubbedZIOMethod0[R](stubbed0Impl[R](f))

  implicit inline def stubbed[T1, R](inline f: T1 => R): StubbedZIOMethod[T1, R] =
    StubbedZIOMethod[T1, R](stubbed1Impl[T1, R](f))

  implicit inline def stubbed[T1, T2, R](inline f: (T1, T2) => R): StubbedZIOMethod[(T1, T2), R] =
    StubbedZIOMethod[(T1, T2), R](stubbed2Impl[T1, T2, R](f))

  implicit inline def stubbed[T1, T2, T3, R](inline f: (T1, T2, T3) => R): StubbedZIOMethod[(T1, T2, T3), R] =
    StubbedZIOMethod(stubbed3Impl[T1, T2, T3, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, R](inline f: (T1, T2, T3, T4) => R): StubbedZIOMethod[(T1, T2, T3, T4), R] =
    StubbedZIOMethod(stubbed4Impl[T1, T2, T3, T4, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, R](inline f: (T1, T2, T3, T4, T5) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5), R] =
    StubbedZIOMethod(stubbed5Impl[T1, T2, T3, T4, T5, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, R](inline f: (T1, T2, T3, T4, T5, T6) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6), R] =
    StubbedZIOMethod(stubbed6Impl[T1, T2, T3, T4, T5, T6, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, R](inline f: (T1, T2, T3, T4, T5, T6, T7) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    StubbedZIOMethod(stubbed7Impl[T1, T2, T3, T4, T5, T6, T7, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    StubbedZIOMethod(stubbed8Impl[T1, T2, T3, T4, T5, T6, T7, T8, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    StubbedZIOMethod(stubbed9Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    StubbedZIOMethod(stubbed10Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    StubbedZIOMethod(stubbed11Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    StubbedZIOMethod(stubbed12Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    StubbedZIOMethod(stubbed13Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    StubbedZIOMethod(stubbed14Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    StubbedZIOMethod(stubbed15Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    StubbedZIOMethod(stubbed16Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    StubbedZIOMethod(stubbed17Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    StubbedZIOMethod(stubbed18Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    StubbedZIOMethod(stubbed19Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    StubbedZIOMethod(stubbed20Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    StubbedZIOMethod(stubbed21Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    StubbedZIOMethod(stubbed22Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f))

}
