package me.hawkweisman.alexandria
package controllers
package auth
package strategies

import model.User

import org.scalatra.auth.strategy.{BasicAuthStrategy, BasicAuthSupport}
import org.scalatra.auth.{ScentrySupport, ScentryConfig}
import org.scalatra.{ScalatraBase}


class AlexandriaBasicAuthStrategy(
  protected override val app: ScalatraBase,
  realm: String)
extends BasicAuthStrategy[User](app, realm) {

  protected def validate(userName: String, password: String): Option[User]

}
