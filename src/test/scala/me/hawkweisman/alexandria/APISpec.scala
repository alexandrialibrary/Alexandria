import com.mchange.v2.c3p0.ComboPooledDataSource

import me.hawkweisman.alexandria.controllers.APIController
import me.hawkweisman.alexandria.controllers.swagger.AlexandriaSwagger

import org.scalatest.{Inside, BeforeAndAfter, Matchers}
import org.scalatra.test.scalatest._

import org.json4s._
import org.json4s.native.JsonMethods._

import me.hawkweisman.alexandria.model.Book

import slick.driver.JdbcDriver.api._

class APISpec extends ScalatraWordSpec
  with Matchers
  with Inside {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  implicit val swagger = new AlexandriaSwagger
  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds)
  addServlet(new APIController(db), "/*")

  "The Books API" when {
    "handling books by ISBN requests" should {
      "correctly handle some example ISBN lookups" in {
        // TODO: assume that the internet is available here
        get("/book/ISBN:9780980200447") {
          status should equal (201)
          val parsedBook = parse(body).extract[Book]
          inside (parsedBook) {
            case Book(isbn, title,subtitle,byline,pages,publishedDate,publisher,weight) =>
              isbn shouldEqual "ISBN:9780980200447"
              title shouldEqual "Slow reading"
              subtitle should not be 'defined
              byline shouldEqual "by John Miedema."
              pages shouldEqual 92
              publisher shouldEqual "Litwin Books"
          }
        }
      }
      "correctly handle a lookup for an ISBN that is already in the database" in {
        get("/book/ISBN:9780980200447") {
          val parsedBook = parse(body).extract[Book]
          inside (parsedBook) {
            case Book(isbn, title,subtitle,byline,pages,publishedDate,publisher,weight) =>
              isbn shouldEqual "ISBN:9780980200447"
              title shouldEqual "Slow reading"
              subtitle should not be 'defined
              byline shouldEqual "by John Miedema."
              pages shouldEqual 92
              publisher shouldEqual "Litwin Books"
          }
        }
        get("/book/ISBN:9780980200447") {
          status should equal (200)
          val parsedBook = parse(body).extract[Book]
          inside (parsedBook) {
            case Book(isbn, title,subtitle,byline,pages,publishedDate,publisher,weight) =>
              isbn shouldEqual "ISBN:9780980200447"
              title shouldEqual "Slow reading"
              subtitle should not be 'defined
              byline shouldEqual "by John Miedema."
              pages shouldEqual 92
              publisher shouldEqual "Litwin Books"
          }
        }
      }
    }
  }

}
