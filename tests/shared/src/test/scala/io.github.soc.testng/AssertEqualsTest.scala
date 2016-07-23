package io.github.soc.testng

import org.testng.Assert._
import org.testng.annotations.Test

import io.github.soc.testng.utils._

class AssertEqualsTest {
  @Test def test(): Unit = {
    assertEquals(true, false)
  }
}

class AssertEqualsTestAssertions extends TestNGTest with FailureFrameworkArgs {

  override val expectedFail: Int = 1
  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("test"),
        testAssertionErrorMsgOutput("test", "expected:<false> but was:<true>"),
        failureEvent,
        testFinishedOutput("test"),
        testRunFinishedOutput,
        done
    )
  }
}
