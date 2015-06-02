import sbt._
import Keys._

object Play2MorphiaPluginBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  val commonSettings = net.virtualvoid.sbt.graph.Plugin.graphSettings
  
  lazy val Play2MorphiaPlugin = Project(
    "play2-morphia-plugin",
    file("."),
    settings = buildSettings ++ Seq(
    libraryDependencies := runtime ++ test,

    publishTo := {
       val mybean = "http://repo.mybean.cn:8081/"
       if (version.value.trim.endsWith("SNAPSHOT"))
         Some("snapshots" at mybean + "artifactory/libs-snapshot-local")
       else
         Some("releases"  at mybean + "artifactory/libs-release-local")
    },
    publishArtifact in Test := false,
    publishMavenStyle := true,
    pomIncludeRepository := { x => false },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
	
      scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-source", "1.8", "-encoding", "utf8"),
	    unmanagedResourceDirectories in Compile <+= baseDirectory( _ / "conf" ),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11,
    )
  ).settings(commonSettings:_*)

  object Resolvers {
    val githubRepository = Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxReleaseRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "releases" asFile)
    val dropboxSnapshotRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "snapshots" asFile)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  }

  object Dependencies {
    val runtime = Seq(
	   "org.mongodb" % "mongodb-driver" % "3.0.2",
       "org.mongodb.morphia" % "morphia" % "0.111" exclude("org.mongodb", "mongo-java-driver"),
       "org.mongodb.morphia" % "morphia-validation" % "0.111" exclude("org.mongodb", "mongo-java-driver") exclude("javax.validation", "validation-api"),
	   "org.mongodb.morphia" % "morphia-logging-slf4j" % "0.111" exclude("org.mongodb", "mongo-java-driver"),
	   "com.typesafe.play" %% "play-java" % "2.4.0" % "provided"
    )
	
    val test = Seq(
  	   "com.typesafe.play" %% "play-test" % "2.4.0" % "test",
	     "junit" % "junit" % "4.12" % "test",
      "org.easytesting" % "fest-assert" % "1.4" % "test"
    )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion = "0.2.4"
    val buildScalaVersion = "2.11.6"
    val crossBuildVersions = Seq("2.11.6", "2.10.4")
    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version := buildVersion,
	  scalaVersion := buildScalaVersion,
	  crossScalaVersions := crossBuildVersions
    )
  }
}
