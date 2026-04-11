/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven.simple

import scala.io.BufferedSource
import scala.io.Source
import scala.xml.NodeSeq
import scala.xml.XML

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class SimpleMojoSpec extends AnyWordSpec with should.Matchers {

  val pluginXML: BufferedSource = Source.fromResource("META-INF/maven/plugin.xml")

  "plugin.xml" should {
    "be generated" in {
      pluginXML should not be null
    }
  }

  val plugin: NodeSeq = XML.load(pluginXML.bufferedReader()) \\ "plugin"
  "Plugin" should {
    "be defined" in {
      plugin should not be empty
    }
    "have correct goal prefix" in {
      (plugin \\ "goalPrefix").text should be("simple")
    }
  }

  val mojo: NodeSeq = plugin \\ "mojos" \\ "mojo"
  "Mojo" should {
    "be defined" in {
      mojo should not be empty
    }
    "have correct implementation" in {
      (mojo \\ "implementation").text should be("com.github.sbt.maven.simple.SimpleMojo")
    }

    "have correct thread-safe attribute" in {
      (mojo \\ "threadSafe").text.toBoolean should be(true)
    }

    "have correct default phase" in {
      (mojo \\ "phase").text should be("generate-sources")
    }

    val params        = mojo \\ "parameters" \\ "parameter"
    val configuration = mojo \\ "configuration"

    "have correct string parameter" in {
      val param = params.find(n => (n \\ "name").text == "string")
      val conf  = configuration \\ "string"
      param should not be empty
      (param.get \\ "type").text should be("java.lang.String")
      (param.get \\ "required").text.toBoolean should be(true)
      (param.get \\ "editable").text.toBoolean should be(true)
      conf \@ "implementation" should be("java.lang.String")
      conf \@ "default-value" should be("empty")
      conf.text should be("${simple.string}")
    }

    "have correct file parameter" in {
      val param = params.find(n => (n \\ "name").text == "directory")
      val conf  = configuration \\ "directory"
      param should not be empty
      (param.get \\ "type").text should be("java.io.File")
      (param.get \\ "required").text.toBoolean should be(true)
      (param.get \\ "editable").text.toBoolean should be(true)
      conf \@ "implementation" should be("java.io.File")
      conf \@ "default-value" should be("${project.build.directory}/generated-sources/simple")
      conf.text should be("${simple.directory}")
    }

    "have correct map parameter" in {
      val param = params.find(n => (n \\ "name").text == "map")
      val conf  = configuration \\ "map"
      param should not be empty
      (param.get \\ "type").text should be("java.util.Map")
      (param.get \\ "required").text.toBoolean should be(false)
      (param.get \\ "editable").text.toBoolean should be(true)
      conf \@ "implementation" should be("java.util.Map")
      conf.text should be("${simple.map}")
    }

    "have correct project parameter" in {
      val param = params.find(n => (n \\ "name").text == "project")
      val conf  = configuration \\ "project"
      param should not be empty
      (param.get \\ "type").text should be("org.apache.maven.project.MavenProject")
      (param.get \\ "required").text.toBoolean should be(true)
      (param.get \\ "editable").text.toBoolean should be(false)
      conf \@ "implementation" should be("org.apache.maven.project.MavenProject")
      conf \@ "default-value" should be("${project}")
      conf.text should be(empty)
    }

    "have correct set parameter" in {
      val param = params.find(n => (n \\ "name").text == "set")
      val conf  = configuration \\ "set"
      param should not be empty
      (param.get \\ "type").text should be("java.util.Set")
      (param.get \\ "required").text.toBoolean should be(false)
      (param.get \\ "editable").text.toBoolean should be(true)
      conf \@ "implementation" should be("java.util.Set")
      conf.text should be("${simple.set}")
    }
  }
}
