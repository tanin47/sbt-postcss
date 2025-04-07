lazy val root =
  Project("plugins", file(".")).aggregate(sbtSvelte, sbtPostcss).dependsOn(sbtSvelte, sbtPostcss)
lazy val sbtSvelte = RootProject(uri("https://github.com/tanin47/sbt-svelte.git#912b8d5ec1bd0848ae8212241f89d2fd84eef9e0"))
lazy val sbtPostcss = RootProject(file("./..").getCanonicalFile.toURI)

addSbtPlugin("com.typesafe.play" % "sbt-plugin"   % "2.9.2")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.0.0")
