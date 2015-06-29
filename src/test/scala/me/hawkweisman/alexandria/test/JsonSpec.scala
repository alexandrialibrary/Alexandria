package me.hawkweisman.alexandria
package test

import model.{Author, Book, ISBN}

import org.json4s._
import org.json4s.native.JsonMethods._

import org.scalatest.{Matchers, OptionValues, TryValues, WordSpec}

class JsonSpec extends WordSpec
  with Matchers
  with TryValues
  with OptionValues {


  "The Book object" when {
    "creating Books from JSON" should {
      "parse a sample JSON blob with a weight correctly" in {
        val json = parse("""{"ISBN:9780980200447": {"publishers": [{"name": "Litwin Books"}], "pagination": "80p.", "identifiers": {"google": ["4LQU1YwhY6kC"], "lccn": ["2008054742"], "openlibrary": ["OL22853304M"], "isbn_13": ["9780980200447", "9781936117369"], "amazon": ["098020044X"], "isbn_10": ["1936117363"], "oclc": ["297222669"], "goodreads": ["6383507"], "librarything": ["8071257"]}, "table_of_contents": [{"title": "The personal nature of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "Slow reading in an information ecology", "label": "", "pagenum": "", "level": 0}, {"title": "The slow movement and slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The psychology of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The practice of slow reading.", "label": "", "pagenum": "", "level": 0}], "links": [{"url": "http://johnmiedema.ca", "title": "Author's Website"}, {"url": "http://litwinbooks.com/slowreading-ch2.php", "title": "Chapter 2"}, {"url": "http://www.powells.com/biblio/91-9781936117369-0", "title": "Get the e-book"}], "weight": "1 grams", "title": "Slow reading", "url": "https://openlibrary.org/books/OL22853304M/Slow_reading", "classifications": {"dewey_decimal_class": ["028/.9"], "lc_classifications": ["Z1003 .M58 2009"]}, "notes": "Includes bibliographical references and index.", "number_of_pages": 92, "cover": {"small": "https://covers.openlibrary.org/b/id/5546156-S.jpg", "large": "https://covers.openlibrary.org/b/id/5546156-L.jpg", "medium": "https://covers.openlibrary.org/b/id/5546156-M.jpg"}, "subjects": [{"url": "https://openlibrary.org/subjects/books_and_reading", "name": "Books and reading"}, {"url": "https://openlibrary.org/subjects/reading", "name": "Reading"}], "publish_date": "March 2009", "key": "/books/OL22853304M", "authors": [{"url": "https://openlibrary.org/authors/OL6548935A/John_Miedema", "name": "John Miedema"}], "by_statement": "by John Miedema.", "publish_places": [{"name": "Duluth, Minn"}]}}""")
        val isbn = ISBN.parse("ISBN:9780980200447").success.value
        val book = Book.fromJson(json, isbn).success.value

        book.title shouldEqual "Slow reading"
        book.subtitle shouldEqual None
        book.weight.value shouldEqual "1 grams"
        book.isbn shouldBe isbn.toString
        book.pages shouldEqual 92
        book.publisher shouldEqual "Litwin Books"
        book.byline shouldEqual "John Miedema"
      }
    }
    "parse a sample JSON blob with a subtitle correctly" in {
      val json = parse("""{"ISBN:0201558025": {"publishers": [{"name": "Addison-Wesley"}], "pagination": "xiii, 657 p. :", "identifiers": {"lccn": ["93040325"], "openlibrary": ["OL1429049M"], "isbn_10": ["0201558025"], "librarything": ["45844"], "goodreads": ["112243"]}, "subtitle": "a foundation for computer science", "title": "Concrete mathematics", "url": "https://openlibrary.org/books/OL1429049M/Concrete_mathematics", "classifications": {"dewey_decimal_class": ["510"], "lc_classifications": ["QA39.2 .G733 1994"]}, "notes": "Includes bibliographical references (p. 604-631) and index.", "number_of_pages": 657, "cover": {"small": "https://covers.openlibrary.org/b/id/135182-S.jpg", "large": "https://covers.openlibrary.org/b/id/135182-L.jpg", "medium": "https://covers.openlibrary.org/b/id/135182-M.jpg"}, "subjects": [{"url": "https://openlibrary.org/subjects/computer_science", "name": "Computer science"}, {"url": "https://openlibrary.org/subjects/mathematics", "name": "Mathematics"}], "publish_date": "1994", "key": "/books/OL1429049M", "authors": [{"url": "https://openlibrary.org/authors/OL720958A/Ronald_L._Graham", "name": "Ronald L. Graham"}, {"url": "https://openlibrary.org/authors/OL229501A/Donald_Knuth", "name": "Donald Knuth"}, {"url": "https://openlibrary.org/authors/OL2669938A/Oren_Patashnik", "name": "Oren Patashnik"}], "by_statement": "Ronald L. Graham, Donald E. Knuth, Oren Patashnik.", "publish_places": [{"name": "Reading, Mass"}], "ebooks": [{"formats": {"djvu": {"url": "https://archive.org/download/concretemathemat00grah_444/concretemathemat00grah_444.djvu", "permission": "restricted"}}, "preview_url": "https://archive.org/details/concretemathemat00grah_444", "availability": "restricted"}]}}""")
      val isbn = ISBN.parse("ISBN:0201558025").success.value
      val book = Book.fromJson(json, isbn).success.value

      book.title shouldEqual "Concrete mathematics"
      book.subtitle.value shouldEqual "a foundation for computer science"
      book.weight shouldEqual None
      book.isbn shouldBe isbn.toString
      book.pages shouldEqual 657
      book.publisher shouldEqual "Addison-Wesley"
      book.byline shouldEqual "Ronald L. Graham, Donald E. Knuth, Oren Patashnik"
    }
  }

  "The Author object" when {
    "creating Authors from JSON" should {
      "parse a sample JSON blob with a single author correctly" in {
        val json = parse("""{"ISBN:9780980200447": {"publishers": [{"name": "Litwin Books"}], "pagination": "80p.", "identifiers": {"google": ["4LQU1YwhY6kC"], "lccn": ["2008054742"], "openlibrary": ["OL22853304M"], "isbn_13": ["9780980200447", "9781936117369"], "amazon": ["098020044X"], "isbn_10": ["1936117363"], "oclc": ["297222669"], "goodreads": ["6383507"], "librarything": ["8071257"]}, "table_of_contents": [{"title": "The personal nature of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "Slow reading in an information ecology", "label": "", "pagenum": "", "level": 0}, {"title": "The slow movement and slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The psychology of slow reading", "label": "", "pagenum": "", "level": 0}, {"title": "The practice of slow reading.", "label": "", "pagenum": "", "level": 0}], "links": [{"url": "http://johnmiedema.ca", "title": "Author's Website"}, {"url": "http://litwinbooks.com/slowreading-ch2.php", "title": "Chapter 2"}, {"url": "http://www.powells.com/biblio/91-9781936117369-0", "title": "Get the e-book"}], "weight": "1 grams", "title": "Slow reading", "url": "https://openlibrary.org/books/OL22853304M/Slow_reading", "classifications": {"dewey_decimal_class": ["028/.9"], "lc_classifications": ["Z1003 .M58 2009"]}, "notes": "Includes bibliographical references and index.", "number_of_pages": 92, "cover": {"small": "https://covers.openlibrary.org/b/id/5546156-S.jpg", "large": "https://covers.openlibrary.org/b/id/5546156-L.jpg", "medium": "https://covers.openlibrary.org/b/id/5546156-M.jpg"}, "subjects": [{"url": "https://openlibrary.org/subjects/books_and_reading", "name": "Books and reading"}, {"url": "https://openlibrary.org/subjects/reading", "name": "Reading"}], "publish_date": "March 2009", "key": "/books/OL22853304M", "authors": [{"url": "https://openlibrary.org/authors/OL6548935A/John_Miedema", "name": "John Miedema"}], "by_statement": "by John Miedema.", "publish_places": [{"name": "Duluth, Minn"}]}}""")
        val isbn = ISBN.parse("ISBN:9780980200447").success.value
        val authors = Author.fromJson(json)

        authors should have size 1
        authors.headOption.value.getFirstName shouldEqual "John"
        authors.headOption.value.getMiddleName shouldBe None
        authors.headOption.value.getLastName shouldEqual "Miedema"
      }
      "parse a sample JSON blob with multiple authors correctly" in {
        val json = parse("""{"ISBN:0201558025": {"publishers": [{"name": "Addison-Wesley"}], "pagination": "xiii, 657 p. :", "identifiers": {"lccn": ["93040325"], "openlibrary": ["OL1429049M"], "isbn_10": ["0201558025"], "librarything": ["45844"], "goodreads": ["112243"]}, "subtitle": "a foundation for computer science", "title": "Concrete mathematics", "url": "https://openlibrary.org/books/OL1429049M/Concrete_mathematics", "classifications": {"dewey_decimal_class": ["510"], "lc_classifications": ["QA39.2 .G733 1994"]}, "notes": "Includes bibliographical references (p. 604-631) and index.", "number_of_pages": 657, "cover": {"small": "https://covers.openlibrary.org/b/id/135182-S.jpg", "large": "https://covers.openlibrary.org/b/id/135182-L.jpg", "medium": "https://covers.openlibrary.org/b/id/135182-M.jpg"}, "subjects": [{"url": "https://openlibrary.org/subjects/computer_science", "name": "Computer science"}, {"url": "https://openlibrary.org/subjects/mathematics", "name": "Mathematics"}], "publish_date": "1994", "key": "/books/OL1429049M", "authors": [{"url": "https://openlibrary.org/authors/OL720958A/Ronald_L._Graham", "name": "Ronald L. Graham"}, {"url": "https://openlibrary.org/authors/OL229501A/Donald_Knuth", "name": "Donald Knuth"}, {"url": "https://openlibrary.org/authors/OL2669938A/Oren_Patashnik", "name": "Oren Patashnik"}], "by_statement": "Ronald L. Graham, Donald E. Knuth, Oren Patashnik.", "publish_places": [{"name": "Reading, Mass"}], "ebooks": [{"formats": {"djvu": {"url": "https://archive.org/download/concretemathemat00grah_444/concretemathemat00grah_444.djvu", "permission": "restricted"}}, "preview_url": "https://archive.org/details/concretemathemat00grah_444", "availability": "restricted"}]}}""")
        val isbn = ISBN.parse("ISBN:0201558025").success.value
        val authors = Author.fromJson(json)

        authors should have size 3
        authors should contain allOf (
          new Author("Donald", "Knuth"),
          new Author("Ronald", "L.", "Graham"),
          new Author("Oren", "Patashnik")
          )
      }
    }
  }
}
