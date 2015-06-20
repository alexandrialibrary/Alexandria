package me.hawkweisman.alexandria
package controllers

import model.Tables._

import org.scalatra._

import scalate.ScalateSupport

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class AppController(db: Database) extends AlexandriaStack {

  get("/") {
    contentType = "text/html"
    jade(
      "main",
      "books" -> ???
    )
  }

}
