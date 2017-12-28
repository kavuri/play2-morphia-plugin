import sbt._
import Keys._
import play.sbt.PlayImport._
import PlayKeys._
import play.sbt.PlayJava

object Play2MorphiaPluginBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  lazy val Play2MorphiaPlugin = Project("play2-morphia-plugin", file(".")).enablePlugins(PlayJava).settings(buildSettings).settings(
    libraryDependencies := runtime ++ test,

    // publishing
    //you should never set crossVersions to false on publicly published Scala artifacts.
    crossPaths := true,
	   
    publishMavenStyle := true,
	
    publishTo := {
        if (version.value.trim.endsWith("SNAPSHOT"))
	     Some(Resolver.file("file",  new File("C:\\mvn-repo\\snapshots")))
       else
	     Some(Resolver.file("file",  new File("C:\\mvn-repo\\releases")))
    },

	  
      scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-source", "1.8", "-encoding", "utf8"),
      unmanagedResourceDirectories in Compile <+= baseDirectory( _ / "conf" ),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11,
  )

  object Resolvers {
    val githubRepository = Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxReleaseRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "releases" asFile)
    val dropboxSnapshotRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "snapshots" asFile)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  }

  object Dependencies {
    val runtime = Seq(
       javaWs,
       "org.mongodb.morphia" % "morphia" % "1.3.1",
       "org.mongodb.morphia" % "morphia-validation" % "1.3.1",
       "org.reflections" % "reflections" % "0.9.11",
       "org.mongodb.morphia" % "morphia-logging-slf4j" % "1.3.1"
    )
	
    val test = Seq(
      "junit" % "junit" % "4.12" % "test",
      "org.easytesting" % "fest-assert" % "1.4" % "test"
    )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion = "0.2.5.8"
    val buildScalaVersion = "2.12.4"
    val crossBuildVersions = Seq("2.12.4")
    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version := buildVersion,
	  scalaVersion := buildScalaVersion,
	  crossScalaVersions := crossBuildVersions
    )
  }
}
