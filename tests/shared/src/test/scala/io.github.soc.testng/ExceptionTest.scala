package io.github.soc.testng

import org.testng.annotations.Test

import io.github.soc.testng.utils._

class ExceptionTest {
  @Test def test(): Unit = {
    throw new IndexOutOfBoundsException("Exception message")
  }
}

class ExceptionTestAssertions extends TestNGTest with FailureFrameworkArgs {

  override val expectedFail: Int = 1
  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("test"),
        testExceptionMsgOutput("test", "Exception message", "java.lang", "IndexOutOfBoundsException"),
        failureEvent,
        testFinishedOutput("test"),
        testRunFinishedOutput,
        done
    )
  }
}
