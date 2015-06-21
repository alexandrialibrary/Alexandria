package me.hawkweisman.alexandria
package model

import scala.util.{ Try, Success, Failure }

import dispatch._, Defaults._

import me.hawkweisman.util.collection.RepeatableSeq

case class ISBN(group: String,pub: String,title: String, prefix: Option[String]) {

  def query = host("openlibrary.org").secure / "api" / "books" <<?Map(
    "jscmd" -> "data",
    "format" -> "json",
    "bibkeys" -> format)

  /**
   * Format the ISBN as a [[String]] suitable for printing
   * @return the ISBN formatted as a [[String]]
   */
  lazy val format: String = prefix match {
    case Some(prefix) => s"ISBN:$prefix$group$pub$title$isbn13CheckValue"
    case None         => s"ISBN:$group$pub$title" + (isbn10CheckValue match {
      case 10 => "X"
      case i  => i.toString
    })

  }

  /**
   * Attempt to look up the book metadata for this ISBN.
   * Book metadata comes from the Google Books API.
   * @return [[Success]] containing a [[Book]] if a book was found for this ISBN,
   */
  lazy val lookup: Future[Book] = Http(query OK as.String) map {
    (resp) => Book.fromJson(resp, this)
  }
  /**
   * Calculate the check value for an ISBN-13 number
   * @return
   */
  lazy val isbn13CheckValue: Int = {
    val v: Seq[(Char,Int)] = s"${prefix.getOrElse("")}$group$pub$title"
      .zip(Seq(1,3).repeat)
    val i: Seq[Int] = for {
      ((d,c)) <- v
    } yield { d.asDigit * c }
    10 - (i.sum % 10)
  }

  lazy val isbn10CheckValue: Int = {
    val v: Seq[(Char,Int)] = s"$group$pub$title" zip (10 until 1 by -1)
    val i: Seq[Int] = for {
      ((d,c)) <- v
    } yield { d.asDigit * c }
    (11 - (i.sum % 11) ) % 11
  }

  override def toString: String = format

}
object ISBN {

  val isbn13 = ("""^(?:ISBN(?:-13)?:?\ ?)?""" + // Optional ISBN/ISBN13 identifier
                """(?=""" +                   // Basic format pre-checks:
                """[0-9]{13}""" +             // - must be 13 digits
                """|""" +                     //  OR
                """(?=(?:[0-9]+[-\ ]){4})"""+ // - must have 4 separator characters
                """[-\ 0-9]{17}""" +          // - out of 17 characters total
                """)""" +                     // End format pre-checks
                """(97[89])[-\ ]?""" +        // ISBN-13 prefix code
                """([0-9]{1,5})[-\ ]?""" +    // Capture group 1: group ID
                """([0-9]+)[-\ ]?""" +        // Capture group 2: publisher ID
                """([0-9]+)[-\ ]?""" +        // Capture group 3: title ID
                """([0-9])""" +               // Capture group 4: Check digit.
                """$""").r

  val isbn10 = ("""^(?:ISBN(?:-10)?:?\ ?)?"""+ // Optional ISBN/ISBN-10 identifier.
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
    case isbn10(group,pub,title,check) =>{
      val isbn = ISBN(group, pub, title, None)
      isbn.isbn10CheckValue match {
        case n if n == (if (check == "X") 10 else check.toInt) => Success(isbn)
        case n => Failure(new NumberFormatException(
          s"Invalid ISBN-10 check value: expected $check, got $n"))
      }
    }
    case isbn13(prefix,group,pub,title,check) => {
      val isbn = ISBN(group, pub, title, Some(prefix))
      isbn.isbn13CheckValue match {
        case n if n == check.toInt => Success(isbn)
        case n => Failure(new NumberFormatException(
          s"Invalid ISBN-13 check value: expected ${check.toInt}, got $n"))
      }
    }
    case _ => Failure(new NumberFormatException(s"$str was not a valid ISBN number"))
  }

}
