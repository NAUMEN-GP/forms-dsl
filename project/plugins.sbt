resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

resolvers ++= Seq("naumen releases" at "http://NAUMEN-GP.github.io/maven/releases")

resolvers ++= Seq("naumen snapshots" at "http://NAUMEN-GP.github.io/maven/snapshots")

addSbtPlugin("com.naumen" %% "xsbt-sh" % "0.1-SNAPSHOT")
