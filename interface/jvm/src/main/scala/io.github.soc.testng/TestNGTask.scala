package io.github.soc.testng

import sbt.testing.{EventHandler, Logger, Task, TaskDef}

case class TestNGTask(taskDef: TaskDef, args: Array[String], runner: TestNGRunner) extends Task {
  val tags: Array[String] = Array()
  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    loggers.foreach(_.debug(s"running for ${taskDef.fullyQualifiedName}"))

    if (runner.state.permissionToExecute.tryAcquire) {
      TestNGInstance.start(
        TestNGInstance loggingTo loggers
                       loadingClassesFrom runner.testClassLoader
                       using args
                       storingEventsIn runner.state.recorder)

      runner.state.testCompletion.countDown
    }

    runner.state.testCompletion.await

    runner.state.recorder.replayTo(eventHandler, taskDef.fullyQualifiedName, loggers)

    Array()
  }
}
