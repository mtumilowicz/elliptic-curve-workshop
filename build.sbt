ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "elliptic-curve-workshop",
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-kernel" % "2.9.0",
      "dev.zio" %% "zio-test" % "2.0.2" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.2" % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.0.2" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
