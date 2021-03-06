package io.github.soc.testng

import sbt.testing._

final class TestNGFramework extends Framework {

  val name: String = "Scala.js TestNG test framework"

  private object TestNGFingerprint extends AnnotatedFingerprint {
    val annotationName: String = "org.testng.annotations.Test"
    val isModule: Boolean = false
  }

  def fingerprints(): Array[Fingerprint] = Array(TestNGFingerprint)

  def runner(args: Array[String], remoteArgs: Array[String], testClassLoader: ClassLoader): Runner =
    new TestNGMasterRunner(args, remoteArgs, testClassLoader, parseRunSettings(args))
  def slaveRunner(args: Array[String], remoteArgs: Array[String], testClassLoader: ClassLoader, send: String => Unit): Runner =
    new TestNGSlaveRunner(args, remoteArgs, testClassLoader, send, parseRunSettings(args))

  def arrayString(arr: Array[String]): String = arr.mkString("Array(", ", ", ")")

  def parseRunSettings(args: Array[String]): RunSettings = {
    var quiet = false
    var verbose = false
    var noColor = false
    var decodeScalaNames = false
    var logAssert = false
    var notLogExceptionClass = false
    var ignoreRunners = "org.junit.runners.Suite"
    var runListener: String = null
    for (str <- args) {
      str match {
        case "-q" => quiet = true
        case "-v" => verbose = true
        case "-n" => noColor = true
        case "-s" => decodeScalaNames = true
        case "-a" => logAssert = true
        case "-c" => notLogExceptionClass = true

        case s if s.startsWith("-tests=") =>
          throw new UnsupportedOperationException("-tests")

        case s if s.startsWith("--tests=") =>
            throw new UnsupportedOperationException("--tests")

        case s if s.startsWith("--ignore-runners=") =>
          ignoreRunners = s.substring(17)

        case s if s.startsWith("--run-listener=") =>
          runListener = s.substring(15)

        case s if s.startsWith("--include-categories=") =>
            throw new UnsupportedOperationException("--include-categories")

        case s if s.startsWith("--exclude-categories=") =>
            throw new UnsupportedOperationException("--exclude-categories")

        case s if s.startsWith("-D") && s.contains("=") =>
            throw new UnsupportedOperationException("-Dkey=value")

        case s if !s.startsWith("-") && !s.startsWith("+") =>
            throw new UnsupportedOperationException(s)

        case _ =>
      }
    }
    for (s <- args) {
      s match {
        case "+q" => quiet = false
        case "+v" => verbose = false
        case "+n" => noColor = false
        case "+s" => decodeScalaNames = false
        case "+a" => logAssert = false
        case "+c" => notLogExceptionClass = false
        case _    =>
      }
    }
    new RunSettings(!noColor, decodeScalaNames, quiet, verbose, logAssert, ignoreRunners, notLogExceptionClass)
  }
}
