# sbt-testng - Testing via TestNG in sbt

[![Maven Central](https://img.shields.io/maven-central/v/io.github.soc/testng-interface_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.soc/testng-interface_2.11)
[![Scala.js](http://scala-js.org/assets/badges/scalajs-0.6.8.svg)](http://scala-js.org)

This is an implementation of the [sbt test interface](https://github.com/sbt/test-interface) for testing with **[TestNG](http://testng.org)**.

If you're developing in Scala, you can use [Specs2](http://specs2.org) and be happy. However, if you're sentenced to Java, TestNG is a very good alternative to JUnit.

## Verson 3 (stable)

### Usage

Thanks to @asflierl, there is now a convenience sbt plugin which greatly simplifies configuring the testng test interface.

Version 3.0.2 works with sbt 0.13.5.

Add the following to your project's `plugins.sbt` file:

```scala
addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.2")
```

Add the following to your project's `build.sbt` file:

```scala
import de.johoop.testngplugin.TestNGPlugin._

testNGSettings
```

You can configure TestNG via the settings keys below.

When done, run your tests in sbt as usual via **`sbt test`**.

### Settings

#### `testNGVersion`

* *Description:* Version of TestNG to use for the tests.
* *Accepts:* `String`
* *Default:* `"6.8.8"`

#### `testNGOutputDirectory`

* *Description:* Where TestNG stores its test result files.
* *Accepts:* `String`
* *Default:* `"target/testng"`

#### `testNGParameters`

* *Description:* Additional TestNG parameters.
* *Accepts:* `Seq[String]`
* *Default:* `Seq()`

#### `testNGSuites`

* *Description:* TestNG test suite file paths (yaml or xml).
* *Accepts:* `Seq[String]`
* *Default:* `Seq("src/test/resources/testng.yaml")`

### Note

TestNG uses its own test runner which works in a very different way compared to the one from sbt. This means that the interface implementation is kind of a hack.

This also means that executing single tests via**`sbt test-only`** won't work. Please use the options of TestNG instead.

## Version 4 (work in progress)

*sbt-testng 4* adds support for Scala.js projects.
To facilitate that, it provides additional artifacts for Scala.js:
- `testng-runtime` is a Scala.js implementation of elements in `org.testng`
- `testng-interface` provides the glue between Scala.js' `test-interface` and `testng-runtime`
- `testng-scalajs-plugin` rewrites TestNG annotations at compile-time as Scala.js doesn't support reflection

### Example – CrossProject

```scala
import io.github.soc.testng.{TestNGPlugin, TestNGScalaJSPlugin}
import io.github.soc.testng.TestNGPlugin.testNGSuites

lazy val yourProject = crossProject.crossType(CrossType.Full).in(file("."))
  .jvmConfigure(_.enablePlugins(TestNGPlugin))
  .jsConfigure (_.enablePlugins(TestNGScalaJSPlugin))
  .settings(commonSettings: _*)
  .jvmSettings(
    // Use TestNGPlugin keys to configure TestNG (JVM only)
    TestNGPlugin.testNGSuites := Seq(((resourceDirectory in Test).value / "testng.xml").absolutePath))
  .jsSettings (
    // ...
  )
```

### Differences between version 3 and version 4

|                             | Version 3                                                 | Version 4                                                 |
| --------------------------- | --------------------------------------------------------- | --------------------------------------------------------- |
| <sub>Scala version</sub>    | 2.9, 2.10, 2.11                                           | 2.10, 2.11, 2.12-M5                                       |
| <sub>Platform support</sub> | Scala (JVM)                                               | Scala (JVM), Scala.js (JavaScript)                        |
| <sub>TestNG config</sub>    | TestNGPlugin keys, xml file, yml file                     | TestNGPlugin keys, xml file, yml file (JVM only)[¹](#1)   |
| <sub>TestNG runtime</sub>   | TestNG library                                            | TestNG library (JVM), testng-runtime (JavaScript)         |
| <sub>Plugin dependency</sub>| <sub>`"de.johoop" % "sbt-testng-plugin" % "3.0.2"`</sub>  | <sub>`"io.github.soc" % "sbt-testng" % "4.0.0-M2"`</sub>  |
| <sub>SBT testOnly</sub>     | Not supported                                             | Not supported (JVM), supported (JavaScript)               |


###### 1

All existing settings and configuration for the JVM from version 3 continue to work and updating the
dependency from

```scala
addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.2")
```

to

```scala
addSbtPlugin("io.github.soc" % "sbt-testng" % "4.0.0-M2")
```
is all what's necessary.

Please note that TestNG-style configuration (e. g. via command line arguments or testng.xml) of
version 3 is not supported on version 4 (Scala.js), only on version 4 (JVM).

Trying to provide test arguments with `testOptions += Tests.Argument(testNGTestFramework, ...)`
won't work on Scala.js.

You can still add test arguments to the JVM-specific part of your build
(via `<your-project>.jvmSettings(testOptions += Tests.Argument(testNGTestFramework, ...))`).

On Scala.js, SBT's `test` task will execute all tests, and its `testOnly` task will execute the specified tests as usual.
