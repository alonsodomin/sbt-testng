package io.github.soc.testng

import sbt.testing._

final class TestNGMasterRunner(
    args: Array[String],
    remoteArgs: Array[String],
    testClassLoader: ClassLoader,
    runSettings: RunSettings)
    extends TestNGBaseRunner(args, remoteArgs, testClassLoader, runSettings) {

  private[this] var registeredCount = 0
  private[this] var slaveCount = 0

  def tasks(taskDefs: Array[TaskDef]): Array[Task] = {
    registeredCount += taskDefs.length
    taskDefs.map(newTask)
  }

  def done(): String = {
    val slaves     = slaveCount
    val registered = registeredCount
    val done       = doneCount

    if (slaves > 0)
      throw new IllegalStateException(s"There are still $slaves slaves running")

    if (registered != done) {
      val msg = s"$registered task(s) were registered, $done were executed"
      throw new IllegalStateException(msg)
    } else {
      ""
    }
  }

  def receiveMessage(msg: String): Option[String] = msg(0) match {
    case 'd' =>
      val slaveDone = TestNGBaseRunner.Done.deserialize(msg.tail)
      doneCount    += slaveDone.done
      passedCount  += slaveDone.passed
      failedCount  += slaveDone.failed
      ignoredCount += slaveDone.ignored
      skippedCount += slaveDone.skipped
      totalCount   += slaveDone.total
      slaveCount   -= 1
      None
  }
}
