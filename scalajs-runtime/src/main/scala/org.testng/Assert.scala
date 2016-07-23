package org.testng

import scala.annotation.tailrec
import java.lang.Iterable
import java.util.{Collection, Iterator}

object Assert {
  def assertEquals[T          ](actual:            T , expected:            T                                 ): Unit = if (actual != expected) throw new AssertionError(s"expected:<$expected> but was:<$actual>")
  def assertEquals[T          ](actual:            T , expected:            T ,                message: String): Unit = if (actual != expected) throw new AssertionError(message)
  def assertEquals[T <: AnyRef](actual:      Array[T], expected:      Array[T]                                ): Unit = if (actual.sameElements(expected)) throw new AssertionError()
  def assertEquals[T <: AnyRef](actual:      Array[T], expected:      Array[T],                message: String): Unit = if (actual.sameElements(expected)) throw new AssertionError(message)
  def assertEquals[T          ](actual: Collection[T], expected: Collection[T]                                ): Unit = if (actual != expected) throw new AssertionError(s"expected:<$expected> but was:<$actual>")
  def assertEquals[T          ](actual: Collection[T], expected: Collection[T],                message: String): Unit = if (actual != expected) throw new AssertionError(message)
  def assertEquals[T          ](actual:   Iterable[T], expected:   Iterable[T]                                ): Unit = if (!equalsIterable(actual, expected)) throw new AssertionError(s"expected:<$expected> but was:<$actual>")
  def assertEquals[T          ](actual:   Iterable[T], expected:   Iterable[T],                message: String): Unit = if (!equalsIterable(actual, expected)) throw new AssertionError(message)
  def assertEquals[T          ](actual:   Iterator[T], expected:   Iterator[T]                                ): Unit = if (!equalsIterator(actual, expected)) throw new AssertionError(s"expected:<$expected> but was:<$actual>")
  def assertEquals[T          ](actual:   Iterator[T], expected:   Iterator[T],                message: String): Unit = if (!equalsIterator(actual, expected)) throw new AssertionError(message)

  def assertEquals[T          ](actual:       Double , expected:       Double , delta: Double                 ): Unit = if (!(math.abs(actual - expected) < math.abs(delta))) throw new AssertionError(s"expected:<$actual> but was:<$expected> delta: $delta")
  def assertEquals[T          ](actual:       Double , expected:       Double , delta: Double, message: String): Unit = if (!(math.abs(actual - expected) < math.abs(delta))) throw new AssertionError(message)
  def assertEquals[T          ](actual:        Float , expected:        Float , delta: Float                  ): Unit = if (!(math.abs(actual - expected) < math.abs(delta))) throw new AssertionError(s"expected:<$actual> but was:<$expected> delta: $delta")
  def assertEquals[T          ](actual:        Float , expected:        Float , delta: Float , message: String): Unit = if (!(math.abs(actual - expected) < math.abs(delta))) throw new AssertionError(message)

  def assertFalse              (condition: Boolean                                                            ): Unit = if (condition) throw new AssertionError(s"expected:<$condition> but was:<${!condition}>")
  def assertFalse              (condition: Boolean                                           , message: String): Unit = if (condition) throw new AssertionError(message)

  def assertSame  [T <: AnyRef](actual:            T , expected:            T                                 ): Unit = if (actual ne expected) throw new AssertionError
  def assertSame  [T <: AnyRef](actual:            T , expected:            T                , message: String): Unit = if (actual ne expected) throw new AssertionError(message)

  def assertTrue               (condition: Boolean                                                            ): Unit = if (!condition) throw new AssertionError(s"expected:<$condition> but was:<${!condition}>")
  def assertTrue               (condition: Boolean                                           , message: String): Unit = if (!condition) throw new AssertionError(message)

  def fail                     (                                                                              ): Unit = throw new AssertionError
  def fail                     (                                                               message: String): Unit = throw new AssertionError(message)

  private def equalsIterable[T](actual: Iterable[T], expected: Iterable[T]): Boolean = {
    val actIt = actual.iterator
    val expIt = expected.iterator
    equalsIterator(actIt, expIt)
  }

  @tailrec
  private def equalsIterator[T](actual: Iterator[T], expected: Iterator[T]): Boolean = {
    val actHasNext = actual.hasNext
    val expHasNext = expected.hasNext
    if (actHasNext != expHasNext) false
    else if (actHasNext == true) {
      val actNext = actual.next()
      val expNext = expected.next()
      if (actNext != expNext) false
      else equalsIterator(actual, expected)
    } else true
  }
}
