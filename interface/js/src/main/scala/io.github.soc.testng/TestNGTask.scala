package io.github.soc.testng

import Ansi._
import sbt.testing._
import org.scalajs.testinterface.TestUtils
import scala.util.{Try, Success, Failure}

final class TestNGTask(val taskDef: TaskDef, runner: TestNGBaseRunner)
    extends sbt.testing.Task {

  def tags: Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger], continuation: Array[Task] => Unit): Unit = {
    continuation(execute(eventHandler, loggers))
  }

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    val fullClassName = taskDef.fullyQualifiedName
    val richLogger = new RichLogger(loggers, runner.runSettings, fullClassName)

    def infoOrDebug(msg: String): Unit = {
      if (runner.runSettings.verbose)
        richLogger.info(msg)
      else
        richLogger.debug(msg)
    }

    infoOrDebug("Test run started")

    val bootstrapperName = fullClassName + "$scalajs$testng$bootstrapper"

    val startTime = System.nanoTime

    def errorWhileLoadingClass(t: Throwable): Unit = {
      richLogger.error("Error while loading test class: " + fullClassName, t)
      val selector = new TestSelector(fullClassName)
      val optThrowable = new OptionalThrowable(t)
      val ev = new TestNGEvent(taskDef, Status.Failure, selector, optThrowable)
      eventHandler.handle(ev)
    }

    Try(TestUtils.loadModule(bootstrapperName, runner.testClassLoader)) match {
      case Success(classMetadata: TestNGTestBootstrapper) =>
        new TestNGExecuteTest(taskDef, runner, classMetadata, richLogger, eventHandler).executeTests()

      case Success(_) =>
        val msg = s"Expected $bootstrapperName to extend TestNGTestBootstrapper"
        errorWhileLoadingClass(new Exception(msg))

      case Failure(exception) =>
        println(s"$exception")
        errorWhileLoadingClass(exception)
    }

    runner.taskDone()

    val time    = System.nanoTime - startTime
    val failed  = runner.testFailedCount
    val ignored = runner.testIgnoredCount
    val skipped = runner.testSkippedCount
    val total   = runner.testTotalCount

    val msg = {
      c("Test run finished: ", INFO) +
      c(s"$failed failed", if (failed == 0) INFO else ERRCOUNT) +
      c(s", ", INFO) +
      c(s"$ignored ignored", if (ignored == 0) INFO else IGNCOUNT) +
      c(s", ", INFO) +
      c(s"$skipped skipped", if (skipped == 0) INFO else IGNCOUNT) +
      c(s", $total total, ${time.toDouble / 1000000000}s", INFO)
    }

    infoOrDebug(msg)
    runner.resetTestCounts()

    Array()
  }
}
