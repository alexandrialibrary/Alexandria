package me.hawkweisman.alexandria.controllers.responses

import org.scalatra.swagger.ResponseMessage

/**
 * Created by hawk on 6/22/15.
 */
case class ModelResponseMessage(
  code: Int,
  message: String,
  responseModel: String) extends ResponseMessage[String]
