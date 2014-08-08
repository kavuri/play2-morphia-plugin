import sbt._
import Keys._

object Play2MorphiaPluginBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  lazy val Play2MorphiaPlugin = Project(
    "play2-morphia-plugin",
    file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies := runtime ++ test,
      publishMavenStyle := true,
      publishTo := {
        if (buildVersion.trim.endsWith("SNAPSHOT"))
          Some(dropboxSnapshotRepository)
        else
          Some(dropboxReleaseRepository)
      },
      scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-source", "1.6", "-encoding", "utf8"),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11
    )
  ).settings()

  object Resolvers {
    val githubRepository = Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxReleaseRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "releases" asFile)
    val dropboxSnapshotRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "snapshots" asFile)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  }

  object Dependencies {
    val runtime = Seq(
      "org.mongodb.morphia" % "morphia" % "0.108",
      "org.mongodb.morphia" % "morphia-logging-slf4j" % "0.108",
      "org.mongodb.morphia" % "morphia-validation" % "0.108",
      "org.mongodb" % "mongo-java-driver" % "2.12.3",
	  "com.typesafe.play" %% "play-java" % "2.3.2" % "provided"
    )
    val test = Seq(
	"com.typesafe.play" %% "play-test" % "2.3.2" % "test"
    )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion = "0.0.16"
    val buildScalaVersion = "2.11.1"
    val crossBuildVersions = Seq("2.11.1", "2.10.4")
    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version := buildVersion,
	  scalaVersion := buildScalaVersion,
	  crossScalaVersions := crossBuildVersions
    )
  }
}
