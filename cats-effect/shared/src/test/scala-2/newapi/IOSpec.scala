package newapi

import cats.effect.IO
import munit.CatsEffectSuite
import org.scalamock.stubs.CatsEffectStubs
import scala.language.higherKinds

class IOSpec extends CatsEffectSuite with CatsEffectStubs {

  trait Foo {
    def zeroArgsIO: IO[Option[String]]

    def oneArgIO(x: Int): IO[Option[String]]

    def twoArgsIO(x: Int, y: String): IO[Option[String]]

    def overloaded(x: Int, y: Boolean): IO[Int]

    def overloaded(x: String): IO[Boolean]

    def overloaded: IO[String]

    def typeArgsOptIO[A](value: A): IO[Option[A]]

    def typeArgsOptIOTwoParams[A](value: A, other: A): IO[Option[A]]
  }


  trait FooBaz[F[_]] {
    def foo(x: Int): F[Int]
  }

  test("zero args") {

    val foo = stub[Foo]
    val result = for {
      _ <- foo.zeroArgsIO.returnsIO(IO.none[String])
      _ <- foo.zeroArgsIO
      _ <- foo.zeroArgsIO
    } yield foo.zeroArgsIO.times

    assertIO(result, 2)
  }

  test("one arg") {
    val foo = stub[Foo]
    val result =
      for {
        _ <- (foo.oneArgIO _).returnsIO {
          case 1 => IO(None)
          case _ => IO(Some("1"))
        }
        _ <- foo.oneArgIO(1)
        _ <- foo.oneArgIO(2)
      } yield ((foo.oneArgIO _).times, (foo.oneArgIO _).calls)

    assertIO(result, (2, List(1, 2)))
  }


  test("two args") {
    val foo = stub[Foo]
    val result =
      for {
        _ <- (foo.twoArgsIO _).returnsIO {
          case (1, "foo") => IO(None)
          case _ => IO(Some("1"))
        }
        _ <- foo.twoArgsIO(1, "foo")
        _ <- foo.twoArgsIO(2, "bar")
        times <- (foo.twoArgsIO _).timesIO
        calls <- (foo.twoArgsIO _).callsIO
      } yield (times, calls)

    assertIO(result, (2, List((1, "foo"), (2, "bar"))))
  }


  test("type args one param") {
    val foo = stub[Foo]
    val result =
      for {
        _ <- (foo.typeArgsOptIO[String] _).returnsIO {
          case "foo" => IO(Some("foo"))
          case _ => IO(None)
        }
        result <- foo.typeArgsOptIO[String]("foo")
        times <- (foo.typeArgsOptIO[String] _).timesIO
        calls <- (foo.typeArgsOptIO[String] _).callsIO
      } yield (result, times, calls)

    assertIO(result, (Some("foo"), 1, List("foo")))

  }

  test("type args two params") {
    val foo = stub[Foo]
    val result =
      for {
        _ <- (foo.typeArgsOptIOTwoParams[Int] _).returnsIO {
          case (1, 2) => IO(Some(1))
          case _ => IO(None)
        }
        result <- foo.typeArgsOptIOTwoParams[Int](1, 2)
        times <- (foo.typeArgsOptIOTwoParams[Int] _).timesIO
        calls <- stubbed(foo.typeArgsOptIOTwoParams[Int] _).callsIO
      } yield (result, times, calls)

    assertIO(result, (Some(1), 1, List((1, 2))))
  }


  test("mock interface with type constructors") {
    val foo = stub[FooBaz[IO]]
    val result =
      for {
        _ <- (foo.foo _).returnsIO(_ => IO.pure(1))
        result <- foo.foo(1)
      } yield result

    assertIO(result, 1)
  }
}