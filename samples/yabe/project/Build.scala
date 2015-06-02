import play.sbt.routes.RoutesCompiler
import play.sbt.routes.RoutesKeys._

import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

    val appName         = "yabe"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "leodagdag"  %% "play2-morphia-plugin"  % "0.2.4",
      "junit" % "junit" % "4.12" % "test",
      "org.easytesting" % "fest-assert" % "1.4" % "test"
    )

	val main = Project(appName, file(".")).enablePlugins(play.PlayJava)	  
    .settings(
        version := appVersion,
		scalaVersion := "2.11.6",
        libraryDependencies ++= appDependencies,

		// Play provides two styles of routers, one expects its actions to be injected, the
        // other, legacy style, accesses its actions statically.
        routesGenerator := InjectedRoutesGenerator
    )

}
