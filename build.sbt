name := "sbt-postcss"

lazy val `sbt-postcss` = (project in file("."))
  .enablePlugins(SbtWebBase)
  .settings(
    scalaVersion := "2.12.20",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json"   % "2.8.1",
      "org.mockito"       % "mockito-core" % "3.0.0" % Test,
      "com.lihaoyi"       %% "utest"       % "0.7.1" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

enablePlugins(SbtWebBase)

description := "sbt-web plugin for executing postcss-cli after the sbt-web source file tasks finish."

addSbtJsEngine("1.3.9")
