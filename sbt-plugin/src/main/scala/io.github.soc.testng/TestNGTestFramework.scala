package io.github.soc.testng

import sbt._

object TestNGTestFramework extends TestFramework("io.github.soc.testng.TestNGFramework") {
  override def toString = "TestNG"
}
