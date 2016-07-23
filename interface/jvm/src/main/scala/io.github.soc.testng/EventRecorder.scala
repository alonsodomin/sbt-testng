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

import sbt.testing.{Event, EventHandler, Logger}

import org.testng.{ITestResult, TestListenerAdapter}

class EventRecorder extends TestListenerAdapter {
  private[this] val basket = collection.mutable.HashMap[String, List[Event]]()

  override def onTestFailure(result: ITestResult): Unit = store(TestNGEvent.failure, result)
  override def onTestSkipped(result: ITestResult): Unit = store(TestNGEvent.skipped, result)
  override def onTestSuccess(result: ITestResult): Unit = store(TestNGEvent.success, result)

  private[this] def store(eventFrom: ITestResult => Event, result: ITestResult): Unit = basket synchronized {
    basket.put(TestNGEvent.classNameOf(result), eventFrom(result) :: basket.getOrElse(TestNGEvent.classNameOf(result), Nil))
  }

  def replayTo(handler: EventHandler, className: String, loggers: Array[Logger]): Unit = basket synchronized {
    basket.remove(className).getOrElse(Nil).foreach(handler.handle)
  }
}
