import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

import Publish.{noArtifactsAndPublishingSettings, libraryPublishSettings, pluginPublishSettings}

object Build {
  val testNGTestFramework = new TestFramework("io.github.soc.testng.TestNGFramework")

  lazy val commonSettings: Seq[Setting[_]] = Seq(
    version            := "4.0.0-M2", // Also change TestNGPlugin!
    organization       := "io.github.soc",
    homepage           := Some(url("https://github.com/sbt/sbt-testng")),
    licenses           += ("BSD 3-Clause License", url("http://opensource.org/licenses/BSD-3-Clause")),
    scalaVersion       := "2.11.8",
    crossScalaVersions := Seq("2.10.6", "2.12.0", "2.11.8"),
    //isSnapshot         := true,
    scalacOptions     ++= Seq("-Xexperimental"),
    scalaJSUseRhino in Global := false
  )

  lazy val root = project.in(file("."))
    .settings(commonSettings: _*)
    .settings(noArtifactsAndPublishingSettings: _*)
    .aggregate(
                          testNGRuntimeJS,
      testNGInterfaceJVM, testNGInterfaceJS,
                          testNGPluginJS,
      testNGTestsJVM,     testNGTestsJS,
                                              testNGPluginSBT
    )

  lazy val testNGInterfaceJVM = testNGInterface.jvm
  lazy val testNGInterfaceJS  = testNGInterface.js.dependsOn(testNGRuntimeJS)

  // A basic implementation of elements in org.testng.
  // This allows people to run TestNG code unchanged on Scala.js.
  lazy val testNGRuntimeJS = Project(
      id   = "testng-scalajs-runtime",
      base = file("scalajs-runtime"))
    .settings(commonSettings)
    .settings(libraryPublishSettings)
    .settings(name := "testng-runtime")
    .enablePlugins(ScalaJSPlugin)

  // The TestNG interface provides implementations for SBT's test-interface library.
  // There is one implementation for Scala-JVM and one for Scala.js.
  lazy val testNGInterface = crossProject.crossType(CrossType.Full).in(file("interface"))
    .settings(commonSettings: _*)
    .settings(libraryPublishSettings: _*)
    .settings(name := "testng-interface")
    .jvmSettings(
      libraryDependencies += "org.scala-sbt"  % "test-interface" % "1.0",
      libraryDependencies += "org.testng"     % "testng"         % "6.9.10")
        //"com.google.inject" % "guice" % "4.1" % "provided"
    .jsSettings(
      libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.13")

  // This is the compiler plugin that transforms test annotations into code at compile time.
  // We can't do this at runtime, as Scala.js lacks the necessary reflection facilities.
  lazy val testNGPluginJS = Project(
      id   = "testNGPluginJS",
      base = file("scalajs-plugin"))
    .settings(commonSettings)
    .settings(libraryPublishSettings)
    .settings(
      name := "testng-scalajs-plugin",
      crossVersion := CrossVersion.full,
      libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      exportJars := true)

  // Unlike junit-interface, sbt-testng is not only a library,
  // but also an SBT plugin to work around TestNGs madness.
  lazy val testNGPluginSBT = Project(
      id = "sbt-testng",
      base = file("sbt-plugin"))
    .settings(commonSettings: _*)
    .settings(pluginPublishSettings: _*)
    .settings(
      sbtPlugin          := true,
      scalaVersion       := "2.10.6",
      crossScalaVersions := Seq("2.10.6"),
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")
    )

  //// TESTS ////
  val commonTestNGTestsSettings = commonSettings ++ noArtifactsAndPublishingSettings ++ Seq(
      publishArtifact           := false,
      parallelExecution in Test := false,
      libraryDependencies +=  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      unmanagedSourceDirectories in Test +=
        baseDirectory.value.getParentFile / "shared/src/test/scala",
        testOptions in Test ++= Seq(Tests.Filter(_.endsWith("Assertions")))
  )

  lazy val testNGTestsJS = Project(
      id = "testNGTestsJS",
      base = file("tests/output-js"))
    .settings(commonTestNGTestsSettings)
    .settings(
        testFrameworks += new TestFramework("io.github.soc.testng.TestNGFramework"),
        libraryDependencies ++= Seq("org.scala-js" %% "scalajs-test-interface" % "0.6.13"))
    .enablePlugins(ScalaJSPlugin).withScalaJSTestNGPlugin.dependsOn(
      testNGRuntimeJS % "test",
      testNGInterfaceJS % "test"
    )

  lazy val testNGTestsJVM = Project(
      id = "testNGTestsJVM",
      base = file("tests/output-jvm"))
    .settings(commonTestNGTestsSettings)
    .settings(
      testFrameworks += testNGTestFramework,
      testOptions in Test += Tests.Argument(testNGTestFramework, "tests/testng.xml", "-d", "tests/test-results", "-usedefaultlisteners", "false"),
      libraryDependencies += "org.scala-sbt" % "test-interface" % "1.0" % "test")
    .dependsOn(testNGInterfaceJVM % "test")


  //// HELPERS ////
  val isGeneratingEclipse =
    scala.util.Properties.envOrElse("GENERATING_ECLIPSE", "false").toBoolean

  implicit class ProjectOps(val project: Project) extends AnyVal {

    def withScalaJSTestNGPlugin: Project = {
      project.settings(
        scalacOptions in Test ++= {
          if (isGeneratingEclipse) {
            Seq.empty
          } else {
            val jar = (packageBin in (testNGPluginJS, Compile)).value
            Seq(s"-Xplugin:$jar")
          }
        }
      )
    }
  }
}
