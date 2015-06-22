package me.hawkweisman.alexandria
package model

import org.json4s._
import org.json4s.native.JsonMethods._

trait Ownable {
  def owner: User
}

case class Book(
  isbn: String, // ISBNs are unique identifiers for a book in the database
  title: String,
  subtitle: Option[String],
  authors: Seq[Author],
  pages: Int,
  deweyDecimals: Seq[String], // TODO: unfortunately these will probably have to be Strings instead
  publishedDate: String,   // "A library is just a box with strings in it" -- Hawk
  publisher: String,
  weight: String
  ) {


  protected[model] def mungedTitle = if (title startsWith "The") {
    title stripPrefix("The") + ", The" }
    else title
}


object Book {
  implicit val formats = DefaultFormats

  def fromJson(json: String, isbn: ISBN): Book = {
    val book = parse(json) \ isbn.toString
    val title = (book \ "title").extract[String]
    val subOpt = book \ "subtitle"
    val authorStr = (book \ "authors" \\ "name").extract[String]
    val pages = (book \ "number_of_pages").extract[Int]
    val deweys = (book \ "classifications" \ "dewey_decimal_class").extract[List[String]]
    val pubDate = (book \ "publish_date").extract[String]
    val pubBy = (book \ "publishers" \\ "name").extract[String]
    val weight = (book \ "weight").extract[String]
    val subtitle = (book \ "subtitle").toOption flatMap {
      case JString(s) => Some(s)
      case _ => None // TODO: this should log that we got a weird thing
    }
    // TODO: this only supports one author because I don't understand OpenLibrary's
    // book JSON at all.
    val author: Author = authorStr split " " match {
      case Array(first, middle, last) => Author(first, Some(middle), last)
      case Array(first, last) => Author(first, None, last)
      case _ => ??? //TODO: maybe return try?
    }
    Book(isbn.format, title, subtitle, Seq(author), pages, deweys, pubDate, pubBy, weight)
  }
}

object TitleOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.mungedTitle compare b.mungedTitle
}
/* // TODO finish
object PublicationOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.published compare b.published
}*/
