import sbt._
import Keys._

object Build extends Build {

  lazy val buildVersion =  "0.0.1-SNAPSHOT"

  lazy val root = Project(id = "forms-dsl", base = file(".")).settings(
    version := buildVersion,

  libraryDependencies += "com.naumen" %% "scala-reflective-utils" % "0.0.1-SNAPSHOT" ,

  libraryDependencies += "org.specs2" %% "specs2" % "2.1.1" % "test",

  scalacOptions in Test ++= Seq("-Yrangepos")           ,


  resolvers += Resolver.sonatypeRepo("releases"),

    organization := "com.naumen",
    mainClass in (Compile, run) := Some("play.core.server.NettyServer")
  )
}