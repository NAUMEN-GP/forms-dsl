name := "scala-reflective-utils"

scalaVersion := "2.10.2"

version := "0.0.1-SNAPSHOT"

libraryDependencies += "com.thoughtworks.paranamer" % "paranamer" % "2.5.6"

libraryDependencies += "org.javassist" % "javassist" % "3.16.1-GA"

libraryDependencies += "org.specs2" %% "specs2" % "2.1.1" % "test"

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases")