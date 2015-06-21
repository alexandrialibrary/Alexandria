import org.scalatest.{Matchers,WordSpec}

import me.hawkweisman.alexandria.model.ISBN

class ISBNSpec extends WordSpec with Matchers {
  "The ISBN class" when {
    "validating ISBN-13 numbers" should {
      "handle a sample ISBN-13 number correctly" in {
        val isbn = ISBN.parse("9780306406157").get
        isbn.isbn13CheckValue shouldEqual 7
      }
    }
    "validating ISBN-10 numbers" should {
      "handle a sample ISBN-10 number correctly" in {
        val isbn = ISBN.parse("0306406152").get
        isbn.isbn10CheckValue shouldEqual 2
      }
    }
  }
}
