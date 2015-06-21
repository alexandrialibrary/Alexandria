package me.hawkweisman.alexandria
package controllers

import model.Tables._
import model.Book

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger.{Swagger,SwaggerSupport}

import org.json4s.{DefaultFormats, Formats}

import scalate.ScalateSupport

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class APIController(db: Database)(implicit val swagger: Swagger) extends AlexandriaStack
  with NativeJsonSupport with SwaggerSupport {

  // Sets up automatic case class to JSON output serialization
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  // "description" string for Swagger
  protected val applicationDescription = "Alexandria is a simple little card catalogue webapp with a terribly pretentious name."

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  val getByISBN = (apiOperation[Book]("getBookByISBN")
  summary "Get a specific book by ISBN"
  notes   "Get a specific book by ISBN if it exists or look up the book from OpenLibrary and add it to the database if it does not already exist."
  parameters (
    pathParam[String]("isbn").description("ISBN number of the book to look up")
  )
  )

  get("/book/:isbn", operation(getByISBN)) {
    NotImplemented("This isn't done yet.")
  }

}
