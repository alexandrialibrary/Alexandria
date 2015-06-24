package me.hawkweisman.alexandria
package controllers

import slick.driver.H2Driver.api.Database

/**
 * Control for serving the Alexandria app.
 * This should only serve one html page.
 *
 * @author Hawk Weisman
 */
case class AppController(db: Database) extends AlexandriaStack {

  get("/") {
    contentType = "text/html"
    jade(
      "main",
      "books" -> ???
    )
  }

}
