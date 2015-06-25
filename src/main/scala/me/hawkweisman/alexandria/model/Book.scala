package me.hawkweisman.alexandria
package model

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Try

trait Ownable {
  def owner: User
}

case class Book(
  isbn: String, // ISBNs are unique identifiers for a book in the database
  title: String,
  subtitle: Option[String],
  byline: String,
  pages: Int,
  published_date: String,   // "A library is just a box with strings in it" -- Hawk
  publisher: String,
  weight: Option[String]
  ) {

  protected[model] def mungedTitle = if (title startsWith "The") {
    title.stripPrefix("The") + ", The" }
    else title
}


object Book {
  implicit val formats = DefaultFormats

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
//    = {
//     val book = json \ isbn.toString
//     val title = (book \ "title").extract[String]
//     val subOpt = book \ "subtitle"
//     val byline = (book \ "by_statement").extract[String]
//     val pages = (book \ "number_of_pages").extract[Int]
//     //val deweys = (book \ "classifications" \ "dewey_decimal_class").extract[List[String]]
//     val pubDate = (book \ "publish_date").extract[String]
//     val pubBy = (book \ "publishers" \\ "name").extract[String]
//     val weight = (book \ "weight").toOption flatMap {
//       case JString(s) => Some(s)
//       case _ => None
//     }
//     val subtitle = (book \ "subtitle").toOption flatMap {
//       case JString(s) => Some(s)
//       case _ => None // TODO: this should log that we got a weird thing
//     }
//
//     Book(isbn.format, title, subtitle, byline, pages, pubDate, pubBy, weight)
//   }
// }

object TitleOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.mungedTitle compare b.mungedTitle
}
/* // TODO finish
object PublicationOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.published compare b.published
}*/
