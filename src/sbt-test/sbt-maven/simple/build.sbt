// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

lazy val checkPluginXml = taskKey[Unit]("Verify that the generated Maven plugin descriptor exists under target.")
lazy val runCompatTests = taskKey[Unit]("Run scripted fixture tests only on sbt versions with a compatible ScalaTest.")

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
    checkPluginXml := {
      val pluginXml =
        if (sbtBinaryVersion.value == "1.0") {
          target.value / "classes" / "META-INF" / "maven" / "plugin.xml"
        } else {
          target.value / "resource_managed" / "main" / "META-INF" / "maven" / "plugin.xml"
      }
      require(pluginXml.isFile, s"Expected generated plugin.xml at $pluginXml")
    },
    runCompatTests := {
      if (sbtBinaryVersion.value == "1.0") {
        (Test / test).value
      } else {
        // TODO Re-enable Test / test on sbt 2 once ScalaTest publishes a Scala 3 version compatible with sbt 2's Scala.
        streams.value.log.info("Skipping Test / test on sbt 2 until a compatible ScalaTest version is available.")
      }
    },
    crossPaths            := false,
    autoScalaLibrary      := false,
    organization          := "com.example",
    name                  := "Simple",
    mavenPluginGoalPrefix := "simple",
    mavenLaunchOpts += version.apply { v => s"-Dplugin.version=$v" }.value,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest"          % "3.2.20" % Test,
      "org.scalatest" %% "scalatest-wordspec" % "3.2.20" % Test,
    )
  )
