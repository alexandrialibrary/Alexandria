package me.hawkweisman.alexandria
package controllers

import me.hawkweisman.util.RichException.makeRich
import me.hawkweisman.util.concurrent.tryToFuture

import responses.{ ModelResponseMessage, ErrorModel, BookSerializer, AuthorSerializer }
import model.Tables._
import model.{ ISBN, Book, Author }

import org.scalatra._
import org.scalatra.json._
import org.scalatra.FutureSupport
import org.scalatra.swagger.{ Swagger, SwaggerSupport, StringResponseMessage }

import org.json4s._

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{ Try, Success, Failure }

import slick.driver.H2Driver.api._

/**
 * Main Scalatra API control.
 *
 * This should be attached at the `/api/` route and handles the
 * books and authors APIs.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
case class APIController(db: Database)(implicit val swagger: Swagger)
  extends AlexandriaStack
  with NativeJsonSupport
  with SwaggerSupport
  with FutureSupport {

  protected implicit def executor: ExecutionContext = global
  // Sets up automatic case class to JSON output serialization
  protected implicit lazy val jsonFormats: Formats = DefaultFormats + BookSerializer + AuthorSerializer

  // "description" string for Swagger
  override protected val applicationName: Option[String] = Some("Books")
  protected val applicationDescription = "Alexandria Books API"

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  // ---- Book API actions ---------------------------------------------------
  val getByISBN = (apiOperation[Book]("getBookByISBN")
    summary "Get a specific book by ISBN"
    notes """Get a specific book by ISBN. If the user has book creation privileges
          |and the ISBN is unrecognized, the book definition is pulled from the
          |Open Library API and stored in the database before returning a book
          |object as normal (but with a different status). If the user doesn't
          |have book creation privileges and the ISBN is unrecognized, a 404
          |is returned.""".stripMargin.replaceAll("\n", " ")
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
      .defaultValue(10),
      queryParam[String]("sort-by")
      .description("""How to sort the returned list. Options are "title" for alphabetical order by title and "date" for publication date.""")
      .optional
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
    response.setHeader("X-Clacks-Overhead", "GNU Terry Pratchett")
    logger debug s"Handling book request for ${params("isbn")}"
    ISBN parse params("isbn") match {
      case Success(isbn) =>
        logger debug s"Successfully parsed ISBN $isbn"
        val bookQuery: Future[Option[Book]] = db run booksByISBNCompiled(isbn)
          .result
          .headOption
        new AsyncResult {
          val is = bookQuery map {
            case Some(book: Book) => // book exists in DB
              logger info s"Found '${book.title}' for ISBN $isbn, sending to client"
              Ok(book) // return 200 OK
            case None => // book does not exist, but query was executed successfully
              logger debug s"Could not find book for ISBN $isbn, querying OpenLibrary"
              isbn.authors flatMap { newAuthors: Seq[Author] =>
                logger info s"Found authors ${newAuthors mkString ", "}, inserting into DB"
                db.run(authors ++= newAuthors)
              } flatMap { (_) =>
                isbn.book
              } flatMap { book: Book =>
                logger info s"Found book' ${book.title}', inserting into DB"
                db.run(books += book) map { _ => Created(book) }
              }
          } recover {
            case why: Throwable =>
              logger error s"Could not create book: $why\n${why.stackTraceString}"
              InternalServerError(ErrorModel fromException (500, why))
          }
        }
      case Failure(why) =>
        logger warn s"Invalid ISBN: ${why.getMessage}\n${why.stackTraceString}"
        BadRequest(ErrorModel.fromException(400, why))
    }

  }

  delete("/book/:isbn", operation(deleteByISBN)) {
    NotImplemented("This isn't done yet.")
  }

  get("/books/?", operation(listBooks)) {
    val offset: Int = params.get("offset") flatMap {
      p: String => Try(p.toInt) toOption
    } getOrElse 0
    val count: Int = params.get("count") flatMap {
      p: String => Try(p.toInt) toOption
    } getOrElse 10
    // build query
    val sortedBooks = params get "sort-by" match {
      case Some("title")  => books.sortBy(_.title.desc)
      case Some("date")   => ??? // todo: this requires dates to be parsed as times
      case _              => books
    }
    val query = db run (if (count > 0) {
      sortedBooks
        .drop(offset)
        .take(count)
        .result
    } else {
      sortedBooks
        .drop(offset)
        .result
    })
    new AsyncResult {
      val is = query map { books =>
        logger debug "Successfully got list of books"
        Ok(books)
      } recover {
        case why: Throwable =>
          InternalServerError(ErrorModel fromException (500, why))
      }
    }
  }

  post("/books/?", operation(createBook)) {
    val newBook: Future[Book] = Try(parse(params("book")).extract[Book])
    val query: Future[Book] = newBook flatMap { book =>
      db run (books += book) map { _ => book }
    }
    new AsyncResult {
      val is = query map { book => // TODO: what if the book was already in the DB?
        logger debug s"Added book $book to database"
        Created(book)
      } recover {
        case _: NoSuchElementException =>
          BadRequest(ErrorModel(400, "No book data was sent"))
        case why: MappingException =>
          BadRequest(ErrorModel fromException (400, why))
        case why: Throwable =>
          InternalServerError(ErrorModel fromException (500, why))
      }
    }
  }

  // ---- Author API actions -------------------------------------------------

  val listAuthors = (apiOperation[Seq[Author]]("listAuthors")
    summary "Get all authors"
    notes "Why would you want to do this? I really don't think you want this."
    parameters (
      queryParam[Int]("offset")
      .description("The starting number of the authors to retrieve")
      .optional
      .defaultValue(0),
      queryParam[Int]("count")
      .description("The number of authors to retrieve")
      .optional
      .defaultValue(10),
      queryParam[String]("sort-by")
      .description("""How to sort the returned list. Options are "first" for first name and "last" for last name.""")
      .optional
      )
    )

  val createAuthor = (apiOperation[Author]("createAuthor")
    summary "Create a new author"
    responseMessage ModelResponseMessage(201, "Author added", "Author")
    parameters bodyParam[Author]("author")
    .description("The author to be added")
    .required)

  val getAuthorByName = (apiOperation[Author]("getAuthorByName")
    summary "Get a specific author by name."
    responseMessage ModelResponseMessage(200, "Author returned", "Author")
    responseMessage StringResponseMessage(404, "Author not found")
    parameters
    pathParam[String]("name")
    .description("The author's name")
    .required
    )

  get("/authors/?", operation(listAuthors)) {
    val offset: Int = params get "offset" flatMap {
      p: String => Try(p.toInt) toOption
    } getOrElse 0
    val count: Int = params get "count" flatMap {
      p: String => Try(p.toInt) toOption
    } getOrElse 10
    val sortedAuthors = params get "sort-by" match {
      case Some("first") => authors.sortBy(_.firstName.desc)
      case Some("last")  => authors.sortBy(_.firstName.desc)
      case _             => authors
    }
    val query = db run (if (count > 0) {
      sortedAuthors
        .drop(offset)
        .take(count)
        .result
    } else {
      sortedAuthors
        .drop(offset)
        .result
    })
    new AsyncResult {
      val is = query map { authors =>
        logger debug "Successfully got list of authors"
        Ok(authors)
      } recover {
        case why: Throwable =>
          InternalServerError(ErrorModel fromException (500, why))
      }
    }
  }

  post("/authors/?", operation(createAuthor)) {
    NotImplemented("This isn't done yet.")
  }

  get("/author/:name", operation(getAuthorByName)) {
    val name: Option[Array[String]]  = params.get("name") map { _ split "-" }
    val first = name map { _.head } getOrElse halt(400, "No first name")
    val last  = name map { _.last } getOrElse halt(400, "No last name")
    val query = db run authorByNameCompiled(first,last)
      .result
      .headOption
    new AsyncResult { val is = query map {
        case Some(author) => Ok(author)
        case None         => NotFound(
          ErrorModel(404, "No authors found matching requested name"))
      } recover {
        case why: Throwable =>
          InternalServerError(ErrorModel fromException (500, why))
      }
    }
  }

}
