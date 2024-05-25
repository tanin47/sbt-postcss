lazy val root =
  Project("plugins", file(".")).aggregate(sbtSvelte, sbtPostcss).dependsOn(sbtSvelte, sbtPostcss)
lazy val sbtSvelte = RootProject(uri("https://github.com/tanin47/sbt-svelte.git#9ea3955ba1da93283e9af8ea59def0b092f173f3"))
lazy val sbtPostcss = RootProject(file("./..").getCanonicalFile.toURI)

addSbtPlugin("com.typesafe.play" % "sbt-plugin"   % "2.9.2")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt" % "2.0.0")
