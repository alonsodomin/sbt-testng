package io.github.soc.testng

import org.testng.annotations.Test

import io.github.soc.testng.utils.TestNGTest

class MethodNameDecodeTest {
  @Test def `abcd ∆ƒ \uD83D\uDE00 * #&$`(): Unit = ()
}

class MethodNameDecodeTestAssertions extends TestNGTest {

  protected def frameworkArgss: List[List[String]] = List(
    List("-v"),
    List("-v", "-n"),
    List("-v", "-s"),
    List("-v", "-s", "-n")
  )

  override val expectedTotal: Int = 1

  protected def expectedOutput(context: OutputContext): List[Output] = {
    import context._
    val methodName =
      if (decodeScalaNames) "abcd ∆ƒ \uD83D\uDE00 * #&$"
      else "abcd$u0020$u2206ƒ$u0020$uD83D$uDE00$u0020$times$u0020$hash$amp$"
    List(
        testRunStartedOutput,
        testStartedOutput(methodName),
        testFinishedOutput(methodName),
        successEvent,
        testRunFinishedOutput,
        done
    )
  }
}
