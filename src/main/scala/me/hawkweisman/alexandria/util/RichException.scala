package me.hawkweisman.alexandria
package util

import java.io.{PrintWriter, StringWriter}

import scala.language.implicitConversions

/**
 * Created by hawk on 6/22/15.
 */
class RichException(val e: Throwable) {
  def stackTraceString: String = {
    val sw = new StringWriter
    e.printStackTrace(new PrintWriter(sw));
    sw toString
  }
}
object RichException {
  implicit def makeRich(e: Throwable): RichException = new RichException(e)
}
