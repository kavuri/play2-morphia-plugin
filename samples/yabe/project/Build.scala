import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

    val appName         = "yabe"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "leodagdag"  %% "play2-morphia-plugin"  % "0.0.16"
    )

	val main = Project(appName, file(".")).enablePlugins(play.PlayJava)	  
    .settings(
        version := appVersion,
		scalaVersion := "2.11.1",
        libraryDependencies ++= appDependencies,
    	resolvers ++= Seq(DefaultMavenRepository, Resolvers.githubRepository)
    )

	object Resolvers {
      val githubRepository = "LeoDagDag repository" at "http://leodagdag.github.com/repository/"
      val dropboxRepository = "Dropbox repository" at "http://dl.dropbox.com/u/18533645/repository/"
	}

}
