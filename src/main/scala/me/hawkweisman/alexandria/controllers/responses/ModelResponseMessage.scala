package me.hawkweisman.alexandria
package controllers
package responses

import org.scalatra.swagger.ResponseMessage

/**
 * Response message with an attached model. This is just for Swagger purposes.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 *
 * @see [[org.scalatra.swagger.ResponseMessage]]
 *
 * Created by hawk on 6/22/15.
 */
final case class ModelResponseMessage(
  code: Int,
  message: String,
  responseModel: String) extends ResponseMessage[String]
