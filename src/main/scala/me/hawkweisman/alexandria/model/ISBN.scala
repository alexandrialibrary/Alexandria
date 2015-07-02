package me.hawkweisman.alexandria
package model

import scala.language.{implicitConversions, postfixOps}
import scala.util.{ Try, Success, Failure }

import dispatch._, Defaults._

import me.hawkweisman.util.collection.RepeatableSeq
import me.hawkweisman.util.concurrent.tryToFuture

import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._

/**
 * Internal representation for an ISBN.
 *
 * Handles conversion between ISBN-10 and ISBN-13, ISBN validation, and looking up ISBNs
 * from OpenLibrary.
 *
 * TODO: consider representing each part of the number as an integer to make these
 *       take up less memory.
 *
 * @param group The group component of the ISBN
 * @param pub   The publisher component of the ISBN
 * @param title The title component of the ISBN
 * @param prefix The prefix component of the ISBN (ISBN-13 only)
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
final case class ISBN(group: String,pub: String,title: String, prefix: Option[String]) {

  /**
   * The query for looking up the book's data from the OpenLibrary API.
   *
   * This is lazy evaluated since there's no need to look up the same query
   * twice.
   *
   * @return the result of the query
   */
  protected lazy val query = host("openlibrary.org").secure / "api" / "books" <<?Map(
    "jscmd" -> "data",
    "format" -> "json",
    "bibkeys" -> format)

  /**
   * Format the ISBN as a [[String]] suitable for printing
   *
   * This is lazy evaluated since there's no need to look up the same query
   * twice.
   *
   * @return the ISBN formatted as a [[String]]
   */
  lazy val format: String = prefix match {
    case Some(thing)  => s"ISBN:$thing$group$pub$title$isbn13CheckValue"
    case None         => s"ISBN:$group$pub$title" + (isbn10CheckValue match {
      case 10 => "X"
      case i  => i.toString
    })

  }

  /**
   * Attempt to look up the book metadata for this ISBN.
   * Book metadata comes from the OpenLibrary API
   *
   * TODO: add fallback data sources if OpenLibrary doesn't have the book
   *
   * @return A [[scala.concurrent.Future Future]] on the parsed JSON
   *         returned by the OpenLibrary API.
   */
  private lazy val lookup: Future[JValue] = Http(query OK as.String) map {
    (resp) => parse(resp)
  }

  /**
   * Get the authors for a given ISBN from OpenLibrary
   */
  lazy val authors: Future[List[Author]] = lookup map { Author fromJson }
  /**
   * Get the book data for a given ISBN from OpenLibrary
   */
  lazy val book: Future[Book] = lookup flatMap { Book.fromJson(_, this) }
  /**
   * Calculate the check value for an ISBN-13 number
   * @return the check value as an Int
   */
  lazy val isbn13CheckValue: Int = {
    val v: Seq[(Char,Int)] = s"${prefix.getOrElse("")}$group$pub$title"
      .zip(Seq(1,3).repeat)
    val i: Seq[Int] = for {
      ((d,c)) <- v
    } yield { d.asDigit * c }
    10 - (i.sum % 10)
  }

  /**
  * Calculate the check value for an ISBN-10 number
  * @return the check value as an Int
  */
  lazy val isbn10CheckValue: Int = {
    val v: Seq[(Char,Int)] = s"$group$pub$title" zip (10 until 1 by -1)
    val i: Seq[Int] = for {
      ((d,c)) <- v
    } yield { d.asDigit * c }
    (11 - (i.sum % 11) ) % 11
  }

  override def toString: String = format

}

/**
 * Companion object for ISBNs
 *
 * @author Hawk Weisman
 */
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
    case isbn10(group,pub,title,check) =>
      val isbn = ISBN(group, pub, title, None)
      isbn.isbn10CheckValue match {
        case n if n == (if (check == "X") 10 else check.toInt) => Success(isbn)
        case n => Failure(new NumberFormatException(
          s"Invalid ISBN-10 check value: expected $check, got $n"))
      }
    case isbn13(prefix,group,pub,title,check) =>
      val isbn = ISBN(group, pub, title, Some(prefix))
      isbn.isbn13CheckValue match {
        case n if n == check.toInt => Success(isbn)
        case n => Failure(new NumberFormatException(
          s"Invalid ISBN-13 check value: expected ${check.toInt}, got $n"))
      }
    case _ => Failure(new NumberFormatException(s"$str was not a valid ISBN number"))
  }

  implicit def isbnAsString(isbn: ISBN): String = isbn.format

}
