import sbt._
import Keys._

object Build extends Build {

  lazy val buildVersion =  "2.1.3"

  lazy val root = Project(id = "forms-dsl", base = file(".")).settings(
    version := buildVersion,

  libraryDependencies += "com.naumen" %% "scala-reflective-utils" % "0.0.1-SNAPSHOT" ,

  libraryDependencies += "org.specs2" %% "specs2" % "2.1.1" % "test",

  scalacOptions in Test ++= Seq("-Yrangepos")           ,


  resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "releases"  at "http://oss.sonatype.org/content/repositories/releases")   ,

    organization := "com.naumen",
    mainClass in (Compile, run) := Some("play.core.server.NettyServer")
  )
}