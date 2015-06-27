package me.hawkweisman.alexandria
package model

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Try

trait Ownable {
  def owner: User
}

/**
 * Internal model for a book.
 *
 *  "A library is just a box with strings in it"
 *    ~ Hawk Weisman
 *
 * @param isbn The book's International Standard Book Number, used to uniquely identify it
 * @param title The book's title
 * @param subtitle An optional subtitle
 * @param byline A String listing the book's authors
 * @param pages The number of pages in the book
 * @param published_date The date the book was published.
 * @param publisher The book's publisher, as a String
 * @param weight The book's weight, as a String
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
case class Book(
  isbn: String, // ISBNs are unique identifiers for a book in the database
  title: String,
  subtitle: Option[String],
  byline: String,
  pages: Int,
  published_date: String, // TODO: find a way to model this that's machineable
  publisher: String,
  weight: Option[String]
  ) {

  /**
   * Removes the word "the" from the book's title.
   *
   * This is to be used for sorting purposes.
   * @return the book's title, with the word "The" stripped
   */
  protected[model] def mungedTitle = if (title startsWith "The") {
    title.stripPrefix("The") + ", The" }
    else title
}


object Book {
  implicit val formats = DefaultFormats

  /**
   * Parses a Book from OpenLibrary JSON.
   *
   * @param json the JSON blob from OpenLibrary
   * @param isbn the book's ISBN
   * @return A `Success[Book]` if the book was parsed successfully,
   *         otherwise a `Failure.`
   */
  def fromJson(json: JValue, isbn: ISBN): Try[Book] = for {
    book <- Try(json \ isbn.toString)
    title <- Try((book \ "title").extract[String])
    subOpt <- Try(book \ "subtitle")
    byline <- Try((book \ "by_statement").extract[String])
    pages <- Try((book \ "number_of_pages").extract[Int])
    pubDate <- Try((book \ "publish_date").extract[String])
    pubBy <- Try((book \ "publishers" \\ "name").extract[String])
    weightOpt <- Try(book \ "weight")
    subtitle <- Try(book \ "subtitle")
  } yield { Book(
    isbn = isbn.format,
    title = title,
    subtitle = subOpt.toOption flatMap {
      case JString(s) => Some(s)
      case _ => None
    },
    byline = byline.stripPrefix("by ").stripSuffix("."),
    pages = pages,
    published_date = pubDate,
    publisher = pubBy,
    weight = weightOpt.toOption flatMap {
      case JString(s) => Some(s)
      case _ => None
    }
  )}
}

/**
 * Ordering for ordering books by their titles alphabetically.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
object TitleOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.mungedTitle compare b.mungedTitle
}
/* // TODO finish
object PublicationOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.published compare b.published
}*/
