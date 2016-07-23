package org.testng.annotations

import java.lang.annotation._

class DataProvider(val name: String) extends scala.annotation.StaticAnnotation with Annotation {
  def annotationType(): Class[_ <: Annotation] = classOf[DataProvider]
}
