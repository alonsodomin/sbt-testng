package io.github.soc.testng

import org.testng.annotations.Test

import io.github.soc.testng.utils.{TestNGTest, SuccessFrameworkArgs}

class TestTest {
  @Test def onlyTest(): Unit = ()
}

class TestTestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("onlyTest"),
        testFinishedOutput("onlyTest"),
        successEvent,
        testRunFinishedOutput,
        done
    )
  }
}
