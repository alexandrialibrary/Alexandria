package me.hawkweisman.alexandria
package test

import model.ISBN
import org.scalatest.{Matchers, TryValues, WordSpec}

class ISBNSpec extends WordSpec
  with Matchers
  with TryValues {

  "The ISBN class" when {
    "given valid ISBN-13 numbers" should {
      "handle a sample ISBN-13 number correctly" in {
        val isbn = ISBN.parse("9780306406157").success.value
        isbn.isbn13CheckValue shouldEqual 7
      }
      "handle a sample ISBN-13 number with dashes correctly" in {
        val isbn = ISBN.parse("978-030-640-615-7").success.value
        isbn.isbn13CheckValue shouldEqual 7
      }
    }
    "given valid ISBN-10 numbers" should {
      "handle a sample ISBN-10 number correctly" in {
        val isbn = ISBN.parse("0306406152").success.value
        isbn.isbn10CheckValue shouldEqual 2
      }
      "handle a sample ISBN-10 number with dashes correctly" in {
        val isbn = ISBN.parse("1-84356-028-3").success.value
        isbn.isbn10CheckValue shouldEqual 3
      }
      "handle a sample ISBN-10 number with check digit X correctly" in {
        val isbn = ISBN.parse("097522980X").success.value
        isbn.isbn10CheckValue shouldEqual 10
      }
    }
    "given an invalid ISBN" should {
      "return a Failure" in {
        ISBN.parse("IMNOTANISBN").failure.exception.getMessage shouldEqual s"IMNOTANISBN was not a valid ISBN number"
      }
    }
  }
}
