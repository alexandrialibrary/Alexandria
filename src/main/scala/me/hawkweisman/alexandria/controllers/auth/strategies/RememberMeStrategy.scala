package me.hawkweisman.alexandria.controllers
package auth
package strategies

import model.User

import org.scalatra.{Cookie, CookieOptions, ScalatraBase}
import org.scalatra.auth.ScentryStrategy

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

class RememberMeStrategy
  (protected val app: ScalatraBase)
  (implicit request: HttpServletRequest, response: HttpServletResponse)
    extends ScentryStrategy[User] {

      override def name: String = "RememberMe"
}
