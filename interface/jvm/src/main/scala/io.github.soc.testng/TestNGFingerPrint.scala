package io.github.soc.testng

import sbt.testing.AnnotatedFingerprint

object TestNGFingerprint extends AnnotatedFingerprint {
    val annotationName = "org.testng.annotations.Test"
    val isModule       = false
  }
