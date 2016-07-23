package io.github.soc.testng

import sbt.testing._

case class TestNGEvent(
    taskDef: TaskDef,
    status: Status,
    selector: Selector,
    throwable: OptionalThrowable = NoThrowable,
    duration: Long = -1L) extends Event {
  def fullyQualifiedName: String = taskDef.fullyQualifiedName
  def fingerprint: Fingerprint = taskDef.fingerprint
}
