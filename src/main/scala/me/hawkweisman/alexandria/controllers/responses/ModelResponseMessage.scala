package me.hawkweisman.alexandria
package controllers
package responses

import org.scalatra.swagger.ResponseMessage

/**
 * Response message with an attached model. This is just for Swagger purposes.
 *
 * @author Hawk Weisman
 *
 * Created by hawk on 6/22/15.
 */
case class ModelResponseMessage(
  code: Int,
  message: String,
  responseModel: String) extends ResponseMessage[String]
