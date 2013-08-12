resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0-SNAPSHOT")

resolvers ++= Seq(
  "fuzion24-releases" at "http://fuzion24.github.io/maven/releases"
)

addSbtPlugin("com.github.hexx" % "sbt-github-repo" % "0.1.0")