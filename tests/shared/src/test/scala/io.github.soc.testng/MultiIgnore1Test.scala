package io.github.soc.testng

import org.testng.annotations.Test
import io.github.soc.testng.utils.{TestNGTest, SuccessFrameworkArgs}

class MultiIgnore1Test {
  @Test(enabled = false) def multiTest1(): Unit = ()
  @Test def multiTest2(): Unit = ()
  @Test def multiTest3(): Unit = ()
  @Test def multiTest4(): Unit = ()
  @Test def multiTest5(): Unit = ()
}

class MultiIgnore1TestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedIgnored: Int = 1
  override val expectedTotal: Int = 4

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testIgnoredOutput("multiTest1"),
        skippedEvent,
        testStartedOutput("multiTest2"),
        testFinishedOutput("multiTest2"),
        successEvent,
        testStartedOutput("multiTest3"),
        testFinishedOutput("multiTest3"),
        successEvent,
        testStartedOutput("multiTest4"),
        testFinishedOutput("multiTest4"),
        successEvent,
        testStartedOutput("multiTest5"),
        testFinishedOutput("multiTest5"),
        successEvent,
        testRunFinishedOutput,
        done
    )
  }
}
