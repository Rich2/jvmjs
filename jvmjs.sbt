/* Copyright 2021 Richard Oliver. Licensed under Apache Licence version 2.0. */

def sett2 = List(
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-deprecation", "-encoding", "UTF-8", "-Xsource:3"),
)

def sett3 = List(
  scalaVersion := "3.0.2-RC1",
  scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-noindent", "-deprecation", "-encoding", "UTF-8"),
)

def util = List(
  Compile/scalaSource := (ThisBuild/baseDirectory).value / "srcUtil"
)

lazy val utilJvm2 = project.settings(sett2).settings(util)

lazy val utilJvm3 = project.settings(sett3).settings(util)

lazy val utilJs2 = project.settings(sett2).settings(util).enablePlugins(ScalaJSPlugin)

lazy val utilJs3 = project.settings(sett3).settings(util).enablePlugins(ScalaJSPlugin)

def graphics = List(
  Compile/scalaSource := (ThisBuild/baseDirectory).value / "srcGraphics"
)

lazy val graphicsJvm2 = project.settings(sett2).settings(graphics).dependsOn(utilJvm2)

lazy val graphicsJvm3 = project.settings(sett3).settings(graphics).dependsOn(utilJvm3)

lazy val graphicsJs2 = project.settings(sett2).settings(graphics).enablePlugins(ScalaJSPlugin).dependsOn(utilJs2)

lazy val graphicsJs3 = project.settings(sett3).settings(graphics).enablePlugins(ScalaJSPlugin).dependsOn(utilJs3)