package io.github.soc.testng

import org.testng.annotations.Test
import io.github.soc.testng.utils.{TestNGTest, SuccessFrameworkArgs}

class MultiIgnoreAllTest {
  @Test(enabled = false) def multiTest1(): Unit = ()
  @Test(enabled = false) def multiTest2(): Unit = ()
  @Test(enabled = false) def multiTest3(): Unit = ()
  @Test(enabled = false) def multiTest4(): Unit = ()
  @Test(enabled = false) def multiTest5(): Unit = ()
}

class MultiIgnoreAllTestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedIgnored: Int = 5

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testIgnoredOutput("multiTest1"),
        skippedEvent,
        testIgnoredOutput("multiTest2"),
        skippedEvent,
        testIgnoredOutput("multiTest3"),
        skippedEvent,
        testIgnoredOutput("multiTest4"),
        skippedEvent,
        testIgnoredOutput("multiTest5"),
        skippedEvent,
        testRunFinishedOutput,
        done
    )
  }
}
