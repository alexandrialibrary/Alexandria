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
  byline: String,
  pages: Int,
  publishedDate: String,   // "A library is just a box with strings in it" -- Hawk
  publisher: String,
  weight: Option[String]
  ) {

  protected[model] def mungedTitle = if (title startsWith "The") {
    title.stripPrefix("The") + ", The" }
    else title
}


object Book {
  implicit val formats = DefaultFormats

  def fromJson(json: JValue, isbn: ISBN): Book = {
    val book = json \ isbn.toString
    val title = (book \ "title").extract[String]
    val subOpt = book \ "subtitle"
    val byline = (book \ "by_statement").extract[String]
    val pages = (book \ "number_of_pages").extract[Int]
    //val deweys = (book \ "classifications" \ "dewey_decimal_class").extract[List[String]]
    val pubDate = (book \ "publish_date").extract[String]
    val pubBy = (book \ "publishers" \\ "name").extract[String]
    val weight = (book \ "weight").toOption flatMap {
      case JString(s) => Some(s)
      case _ => None
    }
    val subtitle = (book \ "subtitle").toOption flatMap {
      case JString(s) => Some(s)
      case _ => None // TODO: this should log that we got a weird thing
    }

    Book(isbn.format, title, subtitle, byline, pages, pubDate, pubBy, weight)
  }
}

object TitleOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.mungedTitle compare b.mungedTitle
}
/* // TODO finish
object PublicationOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.published compare b.published
}*/
