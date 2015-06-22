package me.hawkweisman.alexandria
package controllers

import model.Tables._
import model.{ISBN, Book, Author}
import org.json4s.JsonAST.JValue

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger.{Swagger,SwaggerSupport,ResponseMessage,StringResponseMessage}

import org.json4s.{DefaultFormats, Formats}

import scalate.ScalateSupport
import scala.util.{Try,Success,Failure}

import slick.driver.JdbcDriver.api._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class ModelResponseMessage(
  code: Int,
  message: String,
  responseModel: String) extends ResponseMessage[String]

case class ErrorModel(code: Int, message: String)
object ErrorModel {
  def fromException(code: Int, err: Throwable) = ErrorModel(code, err.getMessage)
}

case class APIController(db: Database)(implicit val swagger: Swagger) extends AlexandriaStack
  with NativeJsonSupport
  with SwaggerSupport {

  // Sets up automatic case class to JSON output serialization
  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  // turn camelCase Scala keys into snake_case JSON keys
  protected override def transformResponseBody(body: JValue): JValue = body.underscoreKeys

  // "description" string for Swagger
  override protected val applicationName: Option[String] = Some("Books")
  protected val applicationDescription = "Alexandria Books API"

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  // ---- Book API actions ---------------------------------------------------
  val getByISBN = (apiOperation[Book]("getBookByISBN")
  summary     "Get a specific book by ISBN"
  notes "Get a specific book by ISBN. If the user has book creation priviliges and the ISBN is unrecognized, the book definition is pulled from the Open Library API and stored in the database before returning a book object as normal (but with a different status). If the user doesn't have book creation privilieges and the ISBN is unrecognized, a 404 is returned."
  responseMessage ModelResponseMessage(200, "Book returned", "Book")
  responseMessage ModelResponseMessage(201, "Book created", "Book")
  responseMessage StringResponseMessage(404, "Book not found")
  responseMessage ModelResponseMessage(400, "Invalid ISBN", "ErrorModel")
  parameters
    pathParam[String]("isbn")
      .description("ISBN number of the book to look up")
      .required

  )

  val deleteByISBN = (apiOperation[Unit]("deleteBookByISBN")
    summary "Delete a specific book by ISBN."
    responseMessage StringResponseMessage(204, "Book deleted")
    parameters
      pathParam[String]("isbn")
        .description("ISBN number of the book to delete")
        .required

  )

  val listBooks = (apiOperation[Seq[Book]]("listBooks")
    summary "Get a list of all books."
    parameters (
      queryParam[Int]("offset")
        .description("The starting number of the books to retrieve")
        .optional
        .defaultValue(0),
      queryParam[Int]("count")
        .description("The number of books to retrieve")
        .optional
        .defaultValue(10)
      )
    )

  val createBook = (apiOperation[Book]("createBook")
    summary "Create a new book"
    parameters
      bodyParam[Book]("book")
        .description("The book to be added to the library")
        .required
  )

    // book API routes -------------------------------------------------------
  get("/book/:isbn", operation(getByISBN)) {
    logger debug s"Handling book request for ${params("isbn")}"
    ISBN parse params("isbn") match {
      case Success(isbn) =>
        logger debug s"Successfully parsed ISBN $isbn"
        val bookByISBN = books filter (_.isbn === isbn.toString)
        NotImplemented("This isn't done yet.")
      case Failure(why) =>
        logger warn s"Invalid ISBN: ${why.getMessage}\n${why.getStackTrace}"
        BadRequest(ErrorModel.fromException(400, why))
    }

  }

  delete("/book/:isbn", operation(deleteByISBN)) {
    NotImplemented("This isn't done yet.")
  }

  get("/books", operation(listBooks)) {
    NotImplemented("This isn't done yet.")
  }

  post("/books", operation(createBook)) {
    NotImplemented("This isn't done yet.")
  }


  // ---- Author API actions -------------------------------------------------

  val listAuthors = (apiOperation[Seq[Author]]("listAuthors")
    summary "Get all authors"
    notes   "Why would you want to do this? I really don't think you want this."
    parameters (
      queryParam[Int]("offset")
        .description("The starting number of the authors to retrieve")
        .optional
        .defaultValue(0),
      queryParam[Int]("count")
        .description("The number of authors to retrieve")
        .optional
        .defaultValue(10)
    )
  )

  val createAuthor = (apiOperation[Author]("createAuthor")
    summary "Create a new author"
    responseMessage ModelResponseMessage(201,"Author added","Author")
    parameters bodyParam[Author]("author")
      .description("The author to be added")
      .required
  )

  val getAuthorByName = (apiOperation[Author]("getAuthorByName")
    summary "Get a specific author by name."
    responseMessage ModelResponseMessage(200,"Author returned","Author")
    responseMessage StringResponseMessage(404,"Author not found")
    parameters
      pathParam[String]("name")
        .description("The author's name")
        .required
  )

  get("/authors/", operation(listAuthors)) {
    NotImplemented("This isn't done yet.")
  }

  post("/authors/", operation(createAuthor)) {
    NotImplemented("This isn't done yet.")
  }

  get("/author/:name", operation(getAuthorByName)) {
    NotImplemented("This isn't done yet.")
  }

}
