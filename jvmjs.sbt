def sett3 = List(
  scalaVersion := "3.0.1-RC2",
  scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-noindent", "-deprecation", "-encoding", "UTF-8"),
)

lazy val utilJvm3 = project.settings(sett3).settings(
  Compile/scalaSource := (ThisBuild/baseDirectory).value / "srcUtil"
)