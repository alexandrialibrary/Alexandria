package me.hawkweisman.alexandria
package controllers
package responses

/**
 * Response model that contains an error message and error status code.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 *
 * Created by hawk on 6/22/15.
 */
final case class ErrorModel(code: Int, message: String)
object ErrorModel {
  def fromException(code: Int, err: Throwable) = ErrorModel(code, err.getMessage)
}
