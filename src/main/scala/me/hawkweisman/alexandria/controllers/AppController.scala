package me.hawkweisman.alexandria
package controllers

import slick.driver.H2Driver.api.Database

case class AppController(db: Database) extends AlexandriaStack {

  get("/") {
    contentType = "text/html"
    jade(
      "main",
      "books" -> ???
    )
  }

}
