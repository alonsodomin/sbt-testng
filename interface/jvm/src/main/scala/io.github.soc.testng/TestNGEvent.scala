/* Copyright (c) 2012 Joachim Hofer & contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The names of the author(s) may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR(S) ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.github.soc.testng

import org.testng.ITestResult

import sbt.testing.{Event, Fingerprint, OptionalThrowable, Selector, Status, TestSelector}

case class TestNGEvent(
    fullyQualifiedName: String,
    fingerprint: Fingerprint,
    selector: Selector,
    status: Status,
    throwable: OptionalThrowable,
    duration: Long) extends Event

object TestNGEvent {
  val failure = (result: ITestResult) => event(Status.Failure, result)
  val skipped = (result: ITestResult) => event(Status.Skipped, result)
  val success = (result: ITestResult) => event(Status.Success, result)

  private[this] def event(status: Status, testResult: ITestResult) =
    TestNGEvent(testResult.getName, TestNGFingerprint, new TestSelector(testResult.getName), status, filterThrowable(status, testResult), testResult.getStartMillis - testResult.getEndMillis)

  // If a test's exceptions was expected, don't pass it along as otherwise the exception seems to get printed.
  private[this] def filterThrowable(status: Status, testNGResult: ITestResult): OptionalThrowable =
    if (status != Status.Success) new OptionalThrowable(testNGResult.getThrowable) else new OptionalThrowable

  def classNameOf(result: ITestResult) = result.getTestClass.getName
}
