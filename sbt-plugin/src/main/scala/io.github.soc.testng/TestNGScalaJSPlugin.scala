package io.github.soc.testng

import sbt._
import sbt.Keys._

import org.scalajs.sbtplugin._
import org.scalajs.sbtplugin.ScalaJSPlugin

object ScalaJSTestNGPlugin extends AutoPlugin {
  override def requires: Plugins = ScalaJSPlugin

  import ScalaJSPlugin.autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    /* The `scala-js-test-plugin` configuration adds a plugin only to the `test`
     * configuration. It is a refinement of the `plugin` configuration which adds
     * it to both `compile` and `test`.
     */
    ivyConfigurations += config("testng-scalajs-plugin").hide,
    libraryDependencies ++= Seq(
        "io.github.soc" %%% "testng-runtime"         % "4.0.0-M0" % "test",
        "io.github.soc" %%% "testng-interface"       % "4.0.0-M0" % "test",
        "io.github.soc"   % "testng-scalajs-plugin"  % "4.0.0-M0" % "testng-scalajs-plugin" cross CrossVersion.full),
    scalacOptions in Test ++= {
      val report = update.value
      val jars = report.select(configurationFilter("testng-scalajs-plugin"))
      for {
        jar <- jars
        jarPath = jar.getPath
        // This is a hack to filter out the dependencies of the plugins
        if jarPath.contains("plugin")
      } yield {
        s"-Xplugin:$jarPath"
      }
    }
  )
}
