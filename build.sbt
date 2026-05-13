// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

import sbtheader.CommentStyle
import sbtheader.FileType
import sbtheader.HeaderPlugin.autoImport.HeaderPattern.commentBetween
import sbtheader.LineCommentCreator

lazy val `sbt-maven-plugin` = project
  .in(file("."))
  .enablePlugins(SbtWebBase)
  .settings(
    scriptedLaunchOpts ++= Seq(
      s"-Dplugin.version=${version.value}",
      "-Xmx128m"
    ),
    scriptedBufferLog := false,
    headerLicense     := Some(
      HeaderLicense.Custom("Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>")
    ),
    Compile / headerMappings ++= Map(
      FileType("sbt")        -> HeaderCommentStyle.cppStyleLineComment,
      FileType("properties") -> HeaderCommentStyle.hashLineComment,
      FileType("md")         -> CommentStyle(new LineCommentCreator("<!---", "-->"), commentBetween("<!---", "*", "-->")),
    ),
    (Compile / headerSources) ++=
      ((baseDirectory.value ** ("*.properties" || "*.md" || "*.sbt"))
        --- (baseDirectory.value ** "target" ** "*")).get ++
        (baseDirectory.value / "project" ** "*.scala" --- (baseDirectory.value ** "target" ** "*")).get ++
        (baseDirectory.value / "src" / "sbt-test" ** ("*.java" || "*.scala" || "*.sbt")).get()
  )

developers += Developer(
  "playframework",
  "The Play Framework Team",
  "contact@playframework.com",
  url("https://github.com/playframework")
)

libraryDependencies ++= Seq(
  "org.apache.maven.plugins" % "maven-plugin-plugin" % "3.15.2",
  "org.apache.maven"         % "maven-core"          % "3.9.15",
  "junit"                    % "junit"               % "4.13.2" % Test
)

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}

crossScalaVersions += "3.8.3"

scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "2.12" => Seq("-Xsource:3", "-release:8")
    case _      => Nil
  }
}

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" =>
      sbtVersion.value
    case _ =>
      "2.0.0-RC12"
  }
}

addCommandAlias(
  "validateCode",
  List(
    "scalafmtSbtCheck",
    "+ scalafmtCheckAll",
    "+ javafmtCheckAll",
    "+ headerCheckAll"
  ).mkString(";")
)
