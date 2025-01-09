# ScalaMock 

Native Scala mocking.

Official website: [scalamock.org](https://scalamock.org/)


## Scala 3 new API (since 7.0.0)
Scalamock internals and new API relies on Scala 3 experimental API, so prerequisites are:
```scala
scalaVersion := "3.4.2" // or higher
Test / scalacOptions += "-experimental"
```

### Dependencies

```scala
libraryDependencies ++= Seq(
  // core module
  "org.scalamock" %% "scalamock" % "7.0.0",
  // zio integration
  "org.scalamock" %% "scalamock-zio" % "7.0.0",
  // cats-effect integration
  "org.scalamock" %% "scalamock-cats-effect" % "7.0.0"
)
```

### Basic API

```scala 3
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.stubs.{Stub, Stubs}

trait Foo:
  def foo: Int
  def foo1(x: Int): Int
  def foo2(x: Int, y: Int): Int

class MySpec extends AnyFunSpec, Matchers, Stubs:

  test("no args"):
    val foo: Stub[Foo] = stub[Foo]
    
    foo.foo.returns(10)
    foo.foo shouldBe 10 // method returns set result
    foo.times shouldBe 1 // number of times method called

  test("one arg"):
    val foo: Stub[Foo] = stub[Foo]
    
    foo.foo1.returns:
      case 1 => 2
      case _ => 0

    foo.foo1(1) shouldBe 2
    foo.foo1(2) shouldBe 0
    foo.times shouldBe 2
    foo.calls shouldBe List(1, 2) // get arguments


  test("two args"):
    val foo: Stub[Foo] = stub[Foo]
    
    foo.foo2.returns:
      case (0, 0) => 1
      case _ => 0
    
    foo.foo2(0, 0) shouldBe 1
    foo.foo2(2, 3) shouldBe 0
    foo.times shouldBe 2
    foo.calls shouldBe List((0, 0), (2, 3)) // get arguments
    foo.times((0, 0)) shouldBe 1 // get number of times arguments caught
      
```

### ZIO API
Dependencies:
```scala
libraryDependencies ++= {
  val zioVersion = "2.1.14"
  Seq(
    "dev.zio" %%% "zio" % zioVersion,
    "dev.zio" %%% "zio-test" % zioVersion % Test,
    "dev.zio" %%% "zio-test-sbt" % zioVersion % Test
  )
}
```

Examples:
```scala 3
import zio.*
import zio.test.*
import org.scalamock.stubs.{Stub, ZIOStubs}

trait Foo:
  def foo: UIO[Int]
  def foo1(x: Int): UIO[Int]
  def foo2(x: Int, y: Int): IO[String, Int]

class MySpec extends ZIOSpecDefault, ZIOStubs:

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("check expectations with zio")(
      test("no args"):
        val foo: Stub[Foo] = stub[Foo]
        for
          _ <- foo.foo.returnsZIO(ZIO.succeed(10))
          _ <- foo.foo.repeatN(10)
          times <- foo.foo.timesZIO
          result = assertTrue(times == 11)
        yield result,
      test("one arg"):
        val foo: Stub[Foo] = stub[Foo]
        for
          _ <- foo.foo1.returnsZIO:
            case 1 => ZIO.succeed(1)
            case _ => ZIO.succeed(0)
          one <- foo.foo1(1)
          two <- foo.foo1(2)
          times <- foo.foo1.timesZIO
          calls <- foo.foo1.callsZIO
          result = assertTrue(
            times == 2, 
            one == 1,
            two == 0,
            calls == List(1, 2)
          )
        yield result,
      test("two args"):
        val foo: Stub[Foo] = stub[Foo]
        for
          _ <- foo.foo2.returnsZIO:
            case (0, 0) => ZIO.succeed(1)
            case _ => ZIO.succeed(0)
          one <- foo.foo2(0, 0)
          two <- foo.foo2(2, 2)
          times <- foo.foo2.timesZIO
          calls <- foo.foo2.callsZIO
          twoZerosTimes <- foo.foo2.timesZIO((0, 0))
          result = assertTrue(
            times == 2, 
            calls == List((0, 0), (2, 2)),
            twoZerosTimes == 1,
            one == 1, 
            two == 0
          )
        yield result
    )
```


### Cats Effect API
Dependencies:
```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.5.7",
  "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
)
```

Examples:
```scala 3
import cats.effect.IO
import munit.CatsEffectSuite
import org.scalamock.stubs.{Stub, CatsEffectStubs}

trait Foo:
  def foo: UIO[Int]
  def foo1(x: Int): UIO[Int]
  def foo2(x: Int, y: Int): IO[String, Int]

class MySpec extends CatsEffectSuite, CatsEffectStubs:
  test("no args"):
    val foo: Stub[Foo] = stub[Foo]
    val times = 
      for
        _ <- foo.foo.returnsIO(IO(10))
        _ <- foo.foo
        _ <- foo.foo
        _ <- foo.foo
        times <- foo.foo.timesIO
      yield times
      
    assertIO(times == 2)
  
  test("one arg"):
    val foo: Stub[Foo] = stub[Foo]
    val result =
      for
        _ <- foo.foo1.returnsIO:
          case 1 => IO(1)
          case _ => IO(0)
        one <- foo.foo1(1)
        two <- foo.foo1(2)
        times <- foo.foo1.timesIO
        calls <- foo.foo1.callsIO
      yield (times, one, two, calls)
    
    assertIO(result, (2, 1, 0, List(1, 2))

  test("two args"):
    val foo: Stub[Foo] = stub[Foo]
    val result = for
      _ <- foo.foo2.returnsIO:
        case (0, 0) => IO(1)
        case _ => IO(0)
      one <- foo.foo2(0, 0)
      two <- foo.foo2(2, 2)
      times <- foo.foo2.timesIO
      calls <- foo.foo2.callsIO
      twoZerosTimes <- foo.foo2.timesIO((0, 0))
    yield (times, calls, twoZerosTimes, one, two)

    assertIO(result, (2, List((0, 0), (2, 2)), 1, 1, 0))

```

## Old API examples

### Expectations-First Style

```scala
test("drawline interaction with turtle") {
  // Create mock Turtle object
  val m = mock[Turtle]
  
  // Set expectations
  m.setPosition.expects(10.0, 10.0).returns(10.0, 10.0)
  m.forward.expects(5.0).returns(())
  m.getPosition.expects().returns(15.0, 10.0)

  // Exercise System Under Test
  drawLine(m, (10.0, 10.0), (15.0, 10.0))
}
```

### Record-then-Verify (Mockito) Style

```scala
test("drawline interaction with turtle") {
  // Create stub Turtle
  val m = stub[Turtle]
  
  // Setup return values
  m.getPosition.when().returns(15.0, 10.0)

  // Exercise System Under Test
  drawLine(m, (10.0, 10.0), (15.0, 10.0))

  // Verify expectations met
  m.setPosition.verify(10.0, 10.0)
  m.forward.verify(5.0)
}
```

A more complete example is on our [Quickstart](http://scalamock.org/quick-start/) page.

## Features

* Fully typesafe
* Full support for Scala features such as:
  * Polymorphic (type parameterised) methods
  * Operators (methods with symbolic names)
  * Overloaded methods
  * Type constraints
* ScalaTest and Specs2 integration
* Mock and Stub support
* Macro Mocks and JVM Proxy Mocks
* Scala.js support
* built for Scala 2.12, 2.13, 3
* Scala 2.10 support was included up to ScalaMock 4.2.0
* Scala 2.11 support was included up to ScalaMock 5.2.0
* Scala 2.12 and 2.13 support was included up to ScalaMock 6.1.1

## Using ScalaMock

Artefacts are published to Maven Central and Sonatype OSS.

For ScalaTest, to use ScalaMock in your Tests, add the following to your `build.sbt`:

```scala
libraryDependencies += Seq(
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test
)
```

## Scala 3 Migration Notes

1. Type should be specified for methods with by-name parameters
```scala 3
trait TestTrait:
  def byNameParam(x: => Int): String

val t = mock[TestTrait]

// this one no longer compiles
(t.byNameParam _).expects(*).returns("")

// this one should be used instead
(t.byNameParam(_: Int)).expects(*).returns("")
``` 

2.    
* Not initialized vars are not supported anymore, use `scala.compiletime.uninitialized` instead
* Vars are **not mockable** anymore

```scala 3
trait X:
  var y: Int  // No longer compiles
  
  var y: Int = scala.compile.uninitialized // Should be used instead
```


 Mocking of non-abstract java classes is not available without workaround

```java
public class JavaClass {
    public int simpleMethod(String b) { return 4; }
}

```

```scala 3
val m = mock[JavaClass] // No longer compiles

class JavaClassExtended extends JavaClass

val mm = mock[JavaClassExtended] // should be used instead
```

3.
* Scala makes use of Symbol.newClass which is marked as experimental; to avoid having to add the `@experimental`
  attribute everywhere in tests, you can add the `Test / scalacOptions += "-experimental"` to your build. Note
  that this option is only available in scala 3.4.0+


## Documentation

For usage in Maven or Gradle, integration with Specs2, and more example examples see the [User Guide](http://scalamock.org/user-guide/)

## Acknowledgements

YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and
[YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).

Many thanks to Jetbrains for providing us with an OSS licence for their fine development 
tools such as [IntelliJ IDEA](https://www.jetbrains.com/idea/).

Also, thanks to https://github.com/fthomas/scala-steward for helping to keep our dependencies updated automatically.
