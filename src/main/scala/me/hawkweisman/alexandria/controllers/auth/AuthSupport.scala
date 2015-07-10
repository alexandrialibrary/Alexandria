package me.hawkweisman.alexandria
package controllers
package auth

import model.User

import com.typesafe.scalalogging.LazyLogging

import org.scalatra.{ScalatraBase}
import org.scalatra.auth.{ScentryConfig, ScentrySupport}

trait AuthSupport
extends ScalatraBase
  with ScentrySupport[User]
  with LazyLogging { self: ScalatraBase =>

}
