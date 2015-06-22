import org.scalatest.{Matchers,WordSpec}

import me.hawkweisman.alexandria.model.{Book,ISBN}

class JsonSpec extends WordSpec with Matchers {
  "The Book object" when {
    "creating Books from JSON" should {
      "parse a sample JSON blob correctly" in {
        val json = """{"ISBN:9780980200447": {"publishers": [{"name": "Litwin Books"}], "pagination": "80p.", "identifiers": {"google": ["4LQU1YwhY6kC"], "lccn": ["2008054742"], "openlibrary": ["OL22853304M"], "isbn_13": ["9780980200447", "9781936117369"], "amazon": ["098020044X"], "isbn_10": ["1936117363"], "oclc": ["297222669"], "goodreads": ["6383507"], "librarything": ["8071257"]}, "table_of_contents": [{"title": "The personal nature of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "Slow reading in an information ecology", "label": "", "pagenum": "", "level": 0}, {"title": "The slow movement and slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The psychology of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The practice of slow reading.", "label": "", "pagenum": "", "level": 0}], "links": [{"url": "http://johnmiedema.ca", "title": "Author's Website"}, {"url": "http://litwinbooks.com/slowreading-ch2.php", "title": "Chapter 2"}, {"url": "http://www.powells.com/biblio/91-9781936117369-0", "title": "Get the e-book"}], "weight": "1 grams", "title": "Slow reading", "url": "https://openlibrary.org/books/OL22853304M/Slow_reading", "classifications": {"dewey_decimal_class": ["028/.9"], "lc_classifications": ["Z1003 .M58 2009"]}, "notes": "Includes bibliographical references and index.", "number_of_pages": 92, "cover": {"small": "https://covers.openlibrary.org/b/id/5546156-S.jpg", "large": "https://covers.openlibrary.org/b/id/5546156-L.jpg", "medium": "https://covers.openlibrary.org/b/id/5546156-M.jpg"}, "subjects": [{"url": "https://openlibrary.org/subjects/books_and_reading", "name": "Books and reading"}, {"url": "https://openlibrary.org/subjects/reading", "name": "Reading"}], "publish_date": "March 2009", "key": "/books/OL22853304M", "authors": [{"url": "https://openlibrary.org/authors/OL6548935A/John_Miedema", "name": "John Miedema"}], "by_statement": "by John Miedema.", "publish_places": [{"name": "Duluth, Minn"}]}}"""

        val isbn = ISBN.parse("ISBN:9780980200447").get
        val book = Book.fromJson(json,isbn)

        book.title shouldEqual "Slow reading"
        book.weight shouldEqual "1 grams"
        book.isbn shouldBe isbn.toString
        book.pages shouldEqual 92
        book.publisher shouldEqual "Litwin Books"
        book.authors should have length 1
        book.authors(0).getFirstName shouldEqual "John"
        book.authors(0).getLastName shouldEqual "Miedema"
      }
    }
  }
}
