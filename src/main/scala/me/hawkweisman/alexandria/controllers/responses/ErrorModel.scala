package me.hawkweisman.alexandria.controllers.responses

/**
 * Created by hawk on 6/22/15.
 */

case class ErrorModel(code: Int, message: String)
object ErrorModel {
  def fromException(code: Int, err: Throwable) = ErrorModel(code, err.getMessage)
}
