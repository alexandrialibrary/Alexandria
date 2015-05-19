package me.hawkweisman.alexandria
package model

import scala.util.Sorting
import java.util.Date


case class Book(
  isbn: ISBN, // ISBNs are unique identifiers for a book in the database
  title: String,
  subtitle: Option[String],
  authors: Seq[Person],
  pages: Int,
  deweys: Seq[Float],
  published: Date,
  checkedOut: Boolean, // todo: check out to a person
  weight: Float
  ) {
  def checkOut: Book = Book(isbn, title, subtitle, authors, pages, deweys, true)
  def checkIn: Book = Book(isbn, title, subtitle, authors, pages, deweys, false)

  lazy val mungedTitle = if (title startsWith "The") {
    title stripPrefix("The") + ", The" }
    else title
}

object TitleOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.mungedTitle compare b.mungedTitle
}
/* // TODO finish
object PublicationOrdering extends Ordering[Book] {
  def compare(a: Book, b: Book) = a.published compare b.published
}*/
