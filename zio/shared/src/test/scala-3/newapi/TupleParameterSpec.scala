package org.scalamock.stubs

import zio.*
import zio.test.*
import org.scalamock.stubs.ZIOStubs
import zio.test.Assertion.*

object TupleParameterSpec extends ZIOSpecDefault, ZIOStubs:
  // Test trait with tuple parameter
  trait TestService:
    def doSomething(input: (Int, Int)): UIO[Int]
    def doSomethingElse(input: (String, Boolean), x: (Int, Int)): UIO[String]

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Tuple parameter tests")(
      test("should stub method with tuple parameter"):
        val service = stub[TestService]
        for
          _ <- service.doSomething.succeedsWith(42)
          result <- service.doSomething((1, 2))
        yield assertTrue(result == 42)
      ,
      test("should stub method with tuple parameter using returns"):
        val service = stub[TestService]
        for
          _ <- service.doSomething.returnsZIO:
            case (a, b) => ZIO.succeed(a + b)
          result <- service.doSomething((10, 20))
        yield assertTrue(result == 30)
      ,
      test("should stub method with different tuple parameter"):
        val service = stub[TestService]
        for
          _ <- service.doSomethingElse.succeedsWith("result")
          result <- service.doSomethingElse(("test", true), (1, 1))
        yield assertTrue(result == "result")
    ) @@ TestAspect.before(ZIO.succeed(resetStubs())) @@ TestAspect.sequential

