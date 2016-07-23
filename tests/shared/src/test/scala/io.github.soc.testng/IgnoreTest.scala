package io.github.soc.testng

import org.testng.Assert._
import org.testng.annotations._

import io.github.soc.testng.utils.{TestNGTest, SuccessFrameworkArgs}

class IgnoreTest {
  @Test(enabled = false) def onlyTest(): Unit = fail
}

class IgnoreTestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedIgnored: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testIgnoredOutput("onlyTest"),
        ignoredEvent,
        testRunFinishedOutput,
        done
    )
  }
}
