package io.github.soc.testng

import org.testng.Assert._
import org.testng.annotations._

import io.github.soc.testng.utils._

@Test
class DataProviderTest {
  @DataProvider(name = "test_data") def data: Array[Array[Any]] = Array(Array(0), Array(2), Array(4))
  @Test(dataProvider = "test_data") def test: Unit = if (false) fail()
  //@Test(dataProvider = "test_data") def test1(x: Int): Unit = if (x % 2 != 0) fail()
}

class DataProviderTestAssertions extends TestNGTest with SuccessFrameworkArgs {

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
