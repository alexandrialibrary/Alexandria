package me.hawkweisman.alexandria
package model

import scala.util.{ Try, Success, Failure }

import dispatch._, Defaults._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import me.hawkweisman.util.collection.RepeatableSeq

case class ISBN(group: String,pub: String,title: String) {

  def query = host("openlibrary.org").secure / "api" / "books" <<?
    Map("jscmd" -> "data", "format" -> "json", "bibkeys" -> s"ISBN:$group$pub$title$isbn13CheckValue")

  /**
   * Format the ISBN as a [[String]] suitable for printing
   * @return the ISBN formatted as a [[String]]
   */
  lazy val format: String = ???

  /**
   * Attempt to look up the book metadata for this ISBN.
   * Book metadata comes from the Google Books API.
   * @return [[Success]] containing a [[Book]] if a book was found for this ISBN,
   */
  lazy val lookup: Future[Book] = Http(query OK as.String) map{
    (resp) =>
      val json = parse(resp)
      // todo: finish parsing
      /*Book(
        isbn = this,
        title = json \ "title",
        subtitle = None
        authors
      )*/???
  }
  /**
   * Calculate the check value for an ISBN-13 number
   * @return
   */
  lazy val isbn13CheckValue: Int = s"$group$pub$title"
    .zip( Seq(1,3).repeat )
    .foldLeft[Int]{0}{
    case (sum: Int, (digit: Char, coeff: Int)) => sum + digit.asDigit + coeff
  } % 10

}
object ISBN {

  val isbn13 = ("""^?:ISBN(?:-13)?:?\ )?""" + // Optional ISBN/ISBN13 identifier
                """(?=""" +                   // Basic format pre-checks:
                """[0-9]{13}""" +             // - must be 13 digits
                """|""" +                     //  OR
                """(?=(?:[0-9]+[-\ ]){4})"""+ // - must have 4 separator characters
                """[-\ 0-9]{17}""" +          // - out of 17 characters total
                """)""" +                     // End format pre-checks
                """97[89][-\ ]?""" +          // ISBN-13 prefix code
                """([0-9]{1,5})[-\ ]?""" +    // Capture group 1: group ID
                """([0-9]+)[-\ ]?""" +        // Capture group 2: publisher ID
                """([0-9]+)[-\ ]?""" +        // Capture group 3: title ID
                """([0-9])""" +               // Capture group 4: Check digit.
                """$""").r

  val isbn10 = ("""^(?:ISBN(?:-10)?:?\ )?"""+ // Optional ISBN/ISBN-10 identifier.
                """(?=""" +                   // Basic format pre-checks:
                """[0-9X]{10}$""" +           // - must be 10 digits/Xs (no separators).
                """|""" +                     //  OR
                """(?=(?:[0-9]+[-\ ]){3})"""+ // - must have 3 separator characters
                """[-\ 0-9X]{13}$""" +        // - out of 13 characters total
                """)""" +                     // End format pre-checks
                """([0-9]{1,5})[-\ ]?""" +    // Capture group 1: group ID
                """([0-9]+)[-\ ]?""" +        // Capture group 2: publisher ID
                """([0-9]+)[-\ ]?""" +        // Capture group 3: title ID
                """([0-9X])""" +              // Capture group 4: Check digit.
                """$""").r

  /**
   * Parse a [[String]] to an [[ISBN]]
   * @return Either a [[Success]] containing an ISBN or a [[Failure]] if the string could not be parsed.
   */
  def parse(str: String): Try[ISBN] = str match {
    case isbn13(group,pub,title,check) => {
      val isbn = ISBN(group, pub, title)
      isbn.isbn13CheckValue match {
        case n if n == check.toInt => Success(isbn)
        case n => Failure(new NumberFormatException(
          s"Invalid ISBN-13 check value: expected ${check.toInt}, got $n"))
      }
    }
    case isbn10(group,pub,title,check) => ???
    case _ => Failure(new NumberFormatException(s"$str was not a valid ISBN number"))
  }


}
