import sbt._


object MyPlugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn(
    uri("git://github.com/NAUMEN-GP/sbt-sh")
    )
}