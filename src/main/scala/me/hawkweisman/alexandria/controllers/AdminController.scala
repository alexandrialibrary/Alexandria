package me.hawkweisman.alexandria
package controllers

import model.Tables._
import model.{Author,Book}

import org.scalatra._
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}

import scalate.ScalateSupport

import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Control for the Alexandria admin app.
 *
 * This gets attached at `/admin/` and handles the admin API and serving
 * the admin app page.
 *
 * @author Hawk Weisman
 */
case class AdminController(db: Database) extends AlexandriaStack
  with NativeJsonSupport
  with FutureSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  protected implicit def executor: ExecutionContext = global

  get("/"){
    logger info "handling request for Admin app"
    contentType = "text/html"
    <html><body>
    <h1>Eventually this will be the admin page.</h1>
    </body></html>
  }

  //////////////////////////////////////////////////////////////////////
  // REMOVE BEFORE FLIGHT //////////////////////////////////////////////
  // This route is for TESTING PURPOSES ONLY ///////////////////////////
  get("/db/create-tables") {
    logger info "got create tables request"
    Await.ready(db.run(createSchemaAction), Duration.Inf)
    logger info "created tables"
    contentType = "text/html"
    <html><body>
    <h1>created tables</h1>
    </body></html>
  } // REMOVE BEFORE FLIGHT ////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////
  // REMOVE BEFORE FLIGHT //////////////////////////////////////////////
  // This route is for TESTING PURPOSES ONLY ///////////////////////////
  get("/db/load-test") {
    val authorsFuture = db run DBIO.seq(
      authors += new Author("John", "Miedema"),
      authors += new Author("Donald", "E.", "Knuth"),
      authors += new Author("Ronald", "L.", "Graham"),
      authors += new Author("Oren", "Patashnik")
    )
    val booksFuture = db run DBIO.seq(
      books += Book(
          isbn          = "ISBN:9780980200447",
          title         = "Slow reading",
          subtitle      = None,
          byline        = "by John Miedema.",
          pages         = 92,
          published_date = "March 2009",
          publisher     = "Litwin Books",
          weight        = Some("1 grams")
        ),
        books += Book(
          isbn          = "ISBN:0201558025",
          title         = "Concrete mathematics",
          subtitle      = Some("a foundation for computer science"),
          byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik.",
          pages         = 657,
          published_date = "1994",
          publisher     = "Addison-Wesley",
          weight        = None
        ),
      books += Book(
        isbn          = "ISBN:9780201896831",
        title         = "The Art of Computer Programming, Vol. 1",
        subtitle      = Some("Fundamental Algorithms"),
        byline        = "by Donald E. Knuth.",
        pages         = 672,
        published_date = "1997",
        publisher     = "Addison-Wesley",
        weight        = Some("2.5 pounds")
      )
    )
    new AsyncResult { val is = for {
        _ <- authorsFuture
        _ <- booksFuture
      } yield {
        Created("it worked")
      }
    }
  }

}
