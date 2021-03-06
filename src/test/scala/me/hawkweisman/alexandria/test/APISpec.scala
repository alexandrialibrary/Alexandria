package me.hawkweisman.alexandria
package test

import java.net.URL

import com.mchange.v2.c3p0.ComboPooledDataSource

import controllers.APIController
import controllers.responses.ErrorModel
import controllers.swagger.AlexandriaSwagger

import tags.{InternetTest, DbTest}

import model.Tables._
import model.{Author, Book}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.scalatest.{Inside, Matchers, OptionValues}
import org.scalatra.test.scalatest._

import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class APISpec
extends ScalatraWordSpec
  with Matchers
  with Inside
  with OptionValues
  with ClearDB {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  implicit val swagger = new AlexandriaSwagger
  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds)
  addServlet(new APIController(db), "/*")

  def createAuthors() = {
    Await.ready( db run DBIO.seq(
      authors += new Author("John", "Miedema"),
      authors += new Author("Donald", "E.", "Knuth"),
      authors += new Author("Ronald", "L.", "Graham"),
      authors += new Author("Oren", "Patashnik")
    ), Duration.Inf)
  }

  "The GET /book/{isbn} route" when {
    "the requested book is not in the database" should {
      "add the book to the database and return it" taggedAs(DbTest, InternetTest) in {
        assume( Try( new URL("https://openlibrary.org").openConnection() ).isSuccess,
          "OpenLibrary API was not reachable"
        )
        get("/book/ISBN:9780980200447") {
          //info(body) //uncomment this if you need to look at the books that are happening
          assume(status != 504, "Test gateway timed out")
          status should equal (201)
          val parsedBook = parse(body).extract[Book]
          inside (parsedBook) {
            case Book(isbn, title,subtitle,byline,pages,published_date,publisher,weight) =>
              isbn shouldEqual "ISBN:9780980200447"
              title shouldEqual "Slow reading"
              subtitle should not be 'defined
              byline shouldEqual "John Miedema"
              pages shouldEqual 92
              publisher shouldEqual "Litwin Books"
              published_date shouldEqual "March 2009"
              weight.value shouldEqual "1 grams"
          }
        }
      }
    }
    "the requested book is already in the database" should {
      "return the requested book" taggedAs(DbTest) in {
        Await.ready(db.run(books += Book(
          isbn          = "ISBN:9780980200447",
          title         = "Slow reading",
          subtitle      = None,
          byline        = "John Miedema",
          pages         = 92,
          published_date = "March 2009",
          publisher     = "Litwin Books",
          weight        = Some("1 grams")
        )), Duration.Inf)
        get("/book/ISBN:9780980200447") {
          assume(status != 504, "Test gateway timed out")
          //info(body) //uncomment this if you need to look at the books that are happening
          status should equal (200)
          val parsedBook = parse(body).extract[Book]
          inside (parsedBook) {
            case Book(isbn, title,subtitle,byline,pages,published_date,publisher,weight) =>
              isbn shouldEqual "ISBN:9780980200447"
              title shouldEqual "Slow reading"
              subtitle should not be 'defined
              byline shouldEqual "John Miedema"
              pages shouldEqual 92
              publisher shouldEqual "Litwin Books"
              published_date shouldEqual "March 2009"
              weight.value shouldEqual "1 grams"
          }
        }
      }
    }
  }
  "The GET /books/ route" when {
    "the requested number of books is greater than the number of books in the database" should {
      "return all the books" taggedAs(DbTest) in {
        Await.ready(db.run(
          books += Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          ), Duration.Inf)
          Await.ready(db.run(
            books += Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
        ), Duration.Inf)

        get("/books/") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          val books = parse(body).extract[Seq[Book]]
          books should have length 2
          books should contain (
            Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          )
          books should contain (
            Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
          )
        }
      }
    }
    "the requested number of books is less than the number of books in the database" should {
      "return only the requested amount" taggedAs DbTest in {
        Await.ready(db.run(
          books += Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          ), Duration.Inf)
          Await.ready(db.run(
            books += Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
        ), Duration.Inf)
        Await.ready(db.run(
          books += Book(
            isbn          = "ISBN:9780201896831",
            title         = "The Art of Computer Programming, Vol. 1",
            subtitle      = Some("Fundamental Algorithms"),
            byline        = "Donald E. Knuth",
            pages         = 672,
            published_date = "1997",
            publisher     = "Addison-Wesley",
            weight        = Some("2.5 pounds")
          )
      ), Duration.Inf)
      get("/books?offset=0&count=2") {
        assume(status != 504, "Test gateway timed out")
        status should equal (200)
        val books = parse(body).extract[Seq[Book]]
        books should have length 2
        books should contain (
          Book(
            isbn          = "ISBN:9780980200447",
            title         = "Slow reading",
            subtitle      = None,
            byline        = "John Miedema",
            pages         = 92,
            published_date = "March 2009",
            publisher     = "Litwin Books",
            weight        = Some("1 grams")
          )
        )
        books should contain (
          Book(
            isbn          = "ISBN:0201558025",
            title         = "Concrete mathematics",
            subtitle      = Some("a foundation for computer science"),
            byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
            pages         = 657,
            published_date = "1994",
            publisher     = "Addison-Wesley",
            weight        = None
          )
        )
        }
      }
      "return the requested amount, starting at an offset" taggedAs DbTest in {
        Await.ready(db.run(
          books += Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          ), Duration.Inf)
          Await.ready(db.run(
            books += Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
        ), Duration.Inf)
        Await.ready(db.run(
          books += Book(
            isbn          = "ISBN:9780201896831",
            title         = "The Art of Computer Programming, Vol. 1",
            subtitle      = Some("Fundamental Algorithms"),
            byline        = "Donald E. Knuth",
            pages         = 672,
            published_date = "1997",
            publisher     = "Addison-Wesley",
            weight        = Some("2.5 pounds")
          )
      ), Duration.Inf)
      get("/books?offset=1&count=2") {
        assume(status != 504, "Test gateway timed out")
        status should equal (200)
        val books = parse(body).extract[Seq[Book]]
        books should have length 2
        books should contain (
          Book(
            isbn          = "ISBN:9780201896831",
            title         = "The Art of Computer Programming, Vol. 1",
            subtitle      = Some("Fundamental Algorithms"),
            byline        = "Donald E. Knuth",
            pages         = 672,
            published_date = "1997",
            publisher     = "Addison-Wesley",
            weight        = Some("2.5 pounds")
          )
        )
        books should contain (
          Book(
            isbn          = "ISBN:0201558025",
            title         = "Concrete mathematics",
            subtitle      = Some("a foundation for computer science"),
            byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
            pages         = 657,
            published_date = "1994",
            publisher     = "Addison-Wesley",
            weight        = None
          )
        )
        }
      }
    }
    "the requested amount is negative" should {
      "return all the books" taggedAs DbTest in {
        Await.ready(db.run(
          books += Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          ), Duration.Inf)
          Await.ready(db.run(
            books += Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
        ), Duration.Inf)
        Await.ready(db.run(
          books += Book(
            isbn          = "ISBN:9780201896831",
            title         = "The Art of Computer Programming, Vol. 1",
            subtitle      = Some("Fundamental Algorithms"),
            byline        = "Donald E. Knuth",
            pages         = 672,
            published_date = "1997",
            publisher     = "Addison-Wesley",
            weight        = Some("2.5 pounds")
          )
      ), Duration.Inf)

        get("/books?offset=0&count=-1") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          val books = parse(body).extract[Seq[Book]]
          books should have length 3
          books should contain (
            Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          )
          books should contain (
            Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
          )
          books should contain (
            Book(
              isbn          = "ISBN:9780201896831",
              title         = "The Art of Computer Programming, Vol. 1",
              subtitle      = Some("Fundamental Algorithms"),
              byline        = "Donald E. Knuth",
              pages         = 672,
              published_date = "1997",
              publisher     = "Addison-Wesley",
              weight        = Some("2.5 pounds")
            )
          )
        }
      }
      "return all the books, starting at an offset" taggedAs DbTest in {
        Await.ready(db.run(
          books += Book(
              isbn          = "ISBN:9780980200447",
              title         = "Slow reading",
              subtitle      = None,
              byline        = "John Miedema",
              pages         = 92,
              published_date = "March 2009",
              publisher     = "Litwin Books",
              weight        = Some("1 grams")
            )
          ), Duration.Inf)
          Await.ready(db.run(
            books += Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
        ), Duration.Inf)
        Await.ready(db.run(
          books += Book(
            isbn          = "ISBN:9780201896831",
            title         = "The Art of Computer Programming, Vol. 1",
            subtitle      = Some("Fundamental Algorithms"),
            byline        = "Donald E. Knuth",
            pages         = 672,
            published_date = "1997",
            publisher     = "Addison-Wesley",
            weight        = Some("2.5 pounds")
          )
      ), Duration.Inf)

        get("/books?offset=1&count=-1") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          val books = parse(body).extract[Seq[Book]]
          books should have length 2
          books should contain (
            Book(
              isbn          = "ISBN:0201558025",
              title         = "Concrete mathematics",
              subtitle      = Some("a foundation for computer science"),
              byline        = "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
              pages         = 657,
              published_date = "1994",
              publisher     = "Addison-Wesley",
              weight        = None
            )
          )
          books should contain (
            Book(
              isbn          = "ISBN:9780201896831",
              title         = "The Art of Computer Programming, Vol. 1",
              subtitle      = Some("Fundamental Algorithms"),
              byline        = "Donald E. Knuth",
              pages         = 672,
              published_date = "1997",
              publisher     = "Addison-Wesley",
              weight        = Some("2.5 pounds")
            )
          )
        }
      }
    }
  }
  "The GET /authors/ route" when {
    "the requested number of authors is greater than the number of authors in the database" should {
      "return all the authors in the database" taggedAs DbTest in {
        createAuthors()

        get("/authors/") {
          status should equal (200)
          assume(status != 504, "Test gateway timed out")
          // info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should have length 4
          authors should contain allOf (
            "Donald E. Knuth",
            "Ronald L. Graham",
            "Oren Patashnik",
            "John Miedema"
            )
        }
      }
    }
    "the requested number of authors is less than the number of authors in the database" should {
      "return the requested amount" taggedAs DbTest in {
        createAuthors()

        get("/authors/?offset=0&count=2") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)

          //info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should have length 2
          authors should contain allOf (
            "John Miedema",
            "Donald E. Knuth"
            )
        }
      }
      "return the requested amount, starting at a given offset" taggedAs DbTest in {
        createAuthors()

        get("/authors/?offset=2&count=2") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          // info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should have length 2
          authors should contain allOf (
            "Ronald L. Graham",
            "Oren Patashnik"
            )
        }
      }
    }
    "the requested number of authors is negative" should {
      "return all the authors in the database" taggedAs DbTest in {
        createAuthors()

        get("/authors/?offset=0&count=-1") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          // info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should have length 4
          authors should contain allOf (
            "Donald E. Knuth",
            "Ronald L. Graham",
            "Oren Patashnik",
            "John Miedema"
            )
        }
      }
    }
    "passed a sort-by parameter" should {
      "sort the authors by first name" taggedAs DbTest in {
        createAuthors()

        get("/authors/?offset=0&count=-1&sort-by=first") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          // info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should contain inOrderOnly (
            "Donald E. Knuth",
            "John Miedema",
            "Oren Patashnik",
            "Ronald L. Graham"
          )
        }
      }
      "sort the authors by last name" taggedAs DbTest in {
        createAuthors()

        get("/authors/?offset=0&count=-1&sort-by=last") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          // info(body)
          val JArray(authorList) = parse(body)
          val authors = authorList map { value: JValue =>
            (value \\ "name").extract[String]
          }
          authors should contain inOrderOnly (
            "Ronald L. Graham",
            "Donald E. Knuth",
            "John Miedema",
            "Oren Patashnik"
          )
        }
      }
      "return an error for invalid sort-by parameters" in {
        get("/authors/?offset=0&count=-1&sort-by=asdf") {
          assume(status != 504, "Test gateway timed out")
          status should equal (400)
          val response = parse(body).extract[ErrorModel]
          response.message shouldEqual "Invalid sort-by param 'asdf'."
        }
      }
    }
  }
  "The GET /author/{name} route" when {
    "the requested author is not in the database" should {
      "return 404" taggedAs DbTest in {
        get("/author/Hawk-Weisman") {
          assume(status != 504, "Test gateway timed out")
          status should equal (404)
        }
      }
    }
    "the requested author is in the database" should {
      "return the requested author for John Miedema" taggedAs DbTest in {
        createAuthors()
        get("/author/John-Miedema") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          (parse(body) \\ "name").extract[String] shouldEqual "John Miedema"
        }
      }
      "return the requested author for Knuth" taggedAs DbTest in {
        createAuthors()
        get("/author/Donald-Knuth") {
          assume(status != 504, "Test gateway timed out")
          status should equal (200)
          (parse(body) \\ "name").extract[String] shouldEqual "Donald E. Knuth"
        }
      }
    }
  }
}
