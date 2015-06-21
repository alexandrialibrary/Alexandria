package me.hawkweisman.alexandria
package controllers

import model.Tables._

import org.scalatra._
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}

import scalate.ScalateSupport

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class AdminController(db: Database) extends AlexandriaStack with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  get("/"){
    contentType = "text/html"
    <html><body>
    <h1>Eventually this will be the admin page.</h1>
    </body></html>
  }

}
