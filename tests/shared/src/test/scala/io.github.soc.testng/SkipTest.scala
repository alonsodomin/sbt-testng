package io.github.soc.testng

import org.testng.SkipException
import org.testng.annotations._

import io.github.soc.testng.utils.{TestNGTest, SuccessFrameworkArgs}

class SkipTest {
  @Test def onlyTest(): Unit = throw new SkipException("This assume should not pass")
}

class SkipTestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedSkipped: Int = 1
  override val expectedTotal:   Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("onlyTest"),
        testAssumptionViolatedOutput("onlyTest"),
        skippedEvent,
        testFinishedOutput("onlyTest"),
        testRunFinishedOutput,
        done
    )
  }
}
