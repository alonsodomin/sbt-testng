package io.github.soc.testng

import org.testng.Assert._
import org.testng.annotations.Test

import io.github.soc.testng.utils._

class AssertFalse2Test {
  @Test def test(): Unit = {
    assertFalse(true, "This is the message")
  }
}

class AssertFalse2TestAssertions extends TestNGTest with FailureFrameworkArgs {

  override val expectedFail: Int = 1
  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("test"),
        testAssertionErrorMsgOutput("test", "This is the message"),
        failureEvent,
        testFinishedOutput("test"),
        testRunFinishedOutput,
        done
    )
  }
}
