package newapi

import org.scalamock.stubs.ZIOStubs
import zio._
import zio.test._

object ZIOSpec extends ZIOSpecDefault with ZIOStubs {

  trait Foo {
    def zeroArgsTask: Task[Option[String]]

    def zeroArgsTaskFail: Task[Option[String]]

    def zeroArgsUIO: UIO[Option[String]]

    def zeroArgsIO: IO[Int, Option[String]]

    def zeroArgsIOFail: IO[Int, Option[String]]

    def oneArgTask(x: Int): Task[Option[String]]

    def oneArgTaskFail(x: Int): Task[Option[String]]

    def oneArgUIO(x: Int): UIO[Option[String]]

    def oneArgIO(x: Int): IO[Int, Option[String]]

    def oneArgIOFail(x: Int): IO[Int, Option[String]]

    def twoArgsTask(x: Int, y: String): Task[Option[String]]

    def twoArgsTaskFail(x: Int, y: String): Task[Option[String]]

    def twoArgsUIO(x: Int, y: String): UIO[Option[String]]

    def twoArgsIO(x: Int, y: String): IO[Int, Option[String]]

    def twoArgsIOFail(x: Int, y: String): IO[Int, Option[String]]

    def overloaded(x: Int, y: Boolean): UIO[Int]

    def overloaded(x: String): UIO[Boolean]

    def overloaded: UIO[String]

    def typeArgsOptUIO[A](value: A): UIO[Option[A]]

    def typeArgsOptUIOTwoParams[A](value: A, other: A): Task[Option[A]]
  }

  val foo = stub[Foo]

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("check expectations with zio")(
      test("zero args") {
        for {
          _ <- foo.zeroArgsTask.returnsZIOWith(ZIO.none)
          _ <- foo.zeroArgsTask.repeatN(10)
          times = foo.zeroArgsTask.times
        } yield assertTrue(times == 11)
      },
      test("two args and cleanup") {
        for {
          _ <- (foo.twoArgsIOFail _).returnsZIO(_ => ZIO.fail(2))
          _ <- foo.twoArgsIOFail(1, "").orElse(ZIO.none)
          calls <- (foo.twoArgsIOFail _).callsZIO
          times2 <- (() => foo.zeroArgsTask).timesZIO
          result = assertTrue(
            calls == List((1, "")),
            times2 == 0
          )
        } yield result
      },
      test("one arg") {
        for {
          _ <- (foo.oneArgIO _).returnsZIO(_ => ZIO.none)
          _ <- foo.oneArgIO(1)
          result = assertTrue(
            (foo.oneArgIO _).times == 1,
            (foo.oneArgIO _).calls == List(1)
          )
        } yield result
      },
      test("type args one param") {
        for {
          _ <- (foo.typeArgsOptUIO[String] _).returnsZIO(_ => ZIO.some("foo"))
          result <- foo.typeArgsOptUIO[String]("foo")
        } yield assertTrue(
          result.contains("foo"),
          (foo.typeArgsOptUIO[String] _).times == 1,
          (foo.typeArgsOptUIO[String] _).calls == List("foo")
        )
      },
      test("type args two params") {
        for {
          _ <- (foo.typeArgsOptUIOTwoParams[Int] _).returnsZIO(_ => ZIO.some(1))
          result <- foo.typeArgsOptUIOTwoParams[Int](1, 2).repeatN(1)
        } yield assertTrue(
          result.contains(1),
          (foo.typeArgsOptUIOTwoParams[Int] _).times == 2,
          (foo.typeArgsOptUIOTwoParams[Int] _).calls == List((1, 2), (1, 2))
        )
      }
    ) @@ TestAspect.before(ZIO.succeed(resetStubs())) @@ TestAspect.sequential
}
