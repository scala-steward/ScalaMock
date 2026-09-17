![GitHub Release](https://img.shields.io/github/v/release/scalamock/scalamock?color=blue])

# scalamock

ScalaMock is native Scala mocking framework.

Documentation is available on [scalamock.org](https://scalamock.org/)


## sbt dependencies

```scala
libraryDependencies ++= Seq(
  // core module
  "org.scalamock" %% "scalamock" % "<latest version in badge>",
  // zio integration
  "org.scalamock" %% "scalamock-zio" % "<latest version in badge>",
  // cats-effect integration
  "org.scalamock" %% "scalamock-cats-effect" % "<latest version in badge>",
  // scalatest integration
  "org.scalamock" %% "scalamock-scalatest" % "<latest version in badge>",
  // specs2 integration (specs2 4.x, Scala 2 and 3)
  "org.scalamock" %% "scalamock-specs2-4" % "<latest version in badge>",
  // specs2 integration (specs2 5.x, Scala 3 only)
  "org.scalamock" %% "scalamock-specs2-5" % "<latest version in badge>"
)
```

Dependencies for other build tools can be found on our website.

## Ask for help

You can ask any question or discuss anything about scalamock in our discord channel on [business4s discord server](https://discord.gg/HRdHS4h9vq)
