package io.github.soc.testng

import org.testng.annotations._

import io.github.soc.testng.utils._

object BeforeAndAfterTest {
  @BeforeClass def beforeClass(): Unit = ()
  @AfterClass def afterClass(): Unit = ()
}

class BeforeAndAfterTest {
  @BeforeMethod def before(): Unit = ()
  @AfterMethod def after(): Unit = ()
  @Test def test(): Unit = ()
}

class BeforeAndAfterTestAssertions extends TestNGTest with SuccessFrameworkArgs {

  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    List(
        testRunStartedOutput,
        testStartedOutput("test"),
        testFinishedOutput("test"),
        successEvent,
        testRunFinishedOutput,
        done
    )
  }
}
