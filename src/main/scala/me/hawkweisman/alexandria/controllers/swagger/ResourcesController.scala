package me.hawkweisman.alexandria
package controllers
package swagger

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, NativeSwaggerBase, Swagger}

class ResourcesController(implicit val swagger: Swagger) extends ScalatraServlet
 with NativeSwaggerBase

object AlexandriaApiInfo extends ApiInfo(
   "Alexandria API",
   "Docs for the Alexandria API",
   "http://github.com/hawkw/alexandria",
   "hi@hawkweisman.me",
   "MIT",
   "http://opensource.org/licenses/MIT"
   )

class AlexandriaSwagger extends Swagger(
  Swagger.SpecVersion,
  "0.0.1-preview",
  AlexandriaApiInfo
  )
