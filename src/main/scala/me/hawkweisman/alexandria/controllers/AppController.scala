package me.hawkweisman.alexandria
package controllers

import slick.driver.H2Driver.api.Database

/**
 * Control for serving the Alexandria app.
 * This should only serve one html page.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
case class AppController(db: Database) extends AlexandriaStack {

  get("/") {
    contentType = "text/html"
    ssp("/app",
      "title"     -> "Alexandria", //TODO: placeholder, get this from config
      "appScript" -> "alexandria-app"
      )
  }

}
