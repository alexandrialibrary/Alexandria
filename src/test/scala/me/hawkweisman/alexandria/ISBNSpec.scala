import org.scalatest.{Matchers,WordSpec,TryValues}

import me.hawkweisman.alexandria.model.ISBN

class ISBNSpec extends WordSpec
  with Matchers
  with TryValues {

  "The ISBN class" when {
    "validating ISBN-13 numbers" should {
      "handle a sample ISBN-13 number correctly" in {
        val isbn = ISBN.parse("9780306406157").success.value
        isbn.isbn13CheckValue shouldEqual 7
      }
    }
    "validating ISBN-10 numbers" should {
      "handle a sample ISBN-10 number correctly" in {
        val isbn = ISBN.parse("0306406152").success.value
        isbn.isbn10CheckValue shouldEqual 2
      }
    }
  }
}
