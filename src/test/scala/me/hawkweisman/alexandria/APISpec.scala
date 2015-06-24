import com.mchange.v2.c3p0.ComboPooledDataSource

import me.hawkweisman.alexandria.controllers.APIController
import me.hawkweisman.alexandria.controllers.swagger.AlexandriaSwagger

import org.scalatest.{Inside, BeforeAndAfter, Matchers,OptionValues}
import org.scalatra.test.scalatest._

import org.json4s._
import org.json4s.native.JsonMethods._

import me.hawkweisman.alexandria.model.Book
import me.hawkweisman.alexandria.model.Tables._

import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration._

class APISpec extends ScalatraWordSpec
  with Matchers
  with Inside
  with BeforeAndAfter
  with OptionValues {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  implicit val swagger = new AlexandriaSwagger
  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds)
  addServlet(new APIController(db), "/*")

  before {
    // block on creating fresh DB before running test
    Await.ready(db run createSchemaAction, Duration.Inf)
  }

  after {
    // block on dropping tables from DB before running next test
    Await.ready(db run dropTablesAction, Duration.Inf)
  }

  "The Books API" when {
    "handling books by ISBN requests" should {
      "correctly handle some example ISBN lookups" in {
        // TODO: assume that the internet is available here
        get("/book/ISBN:9780980200447") {
          //info(body) //uncomment this if you need to look at the books that are happening
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
              publishedDate shouldEqual "March 2009"
              weight.value shouldEqual "1 grams"
          }
        }
      }
      "correctly handle a lookup for an ISBN that is already in the database" in {
        Await.ready(db.run(books += Book(
          "ISBN:9780980200447",
          "Slow reading",
          None,
          "by John Miedema.",
          92,
          "March 2009",
          "Litwin Books",
          Some("1 grams")
        )), Duration.Inf)
        get("/book/ISBN:9780980200447") {
          //info(body) //uncomment this if you need to look at the books that are happening
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
              publishedDate shouldEqual "March 2009"
              weight.value shouldEqual "1 grams"
          }
        }
      }
    }
    "handling requests for a list of books" should {
      "return a list of books from the database" in {
        // todo: finish
        Await.ready(db.run(
          books ++= Seq(
            Book(
              "ISBN:9780980200447",
              "Slow reading",
              None,
              "by John Miedema.",
              92,
              "March 2009",
              "Litwin Books",
              Some("1 grams")),
            Book(
              isbn = "ISBN:0201558025",
              title = "Concrete mathematics",
              subtitle = Some("a foundation for computer science"),
              byline = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik.",
              pages = 657,
              publishedDate = "1994",
              publisher = "Addison-Wesley",
              weight = None
            )
        )), Duration.Inf)
      }
    }
  }

}
