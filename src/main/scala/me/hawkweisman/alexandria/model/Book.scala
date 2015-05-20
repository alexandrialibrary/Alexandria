package me.hawkweisman.alexandria
package model

import scala.util.Sorting
import java.util.Date


case class Book(
  isbn: ISBN, // ISBNs are unique identifiers for a book in the database
  title: String,
  subtitle: Option[String],
  authors: Seq[Author],
  pages: Int,
  deweys: Seq[Float],
  published: Date,
  checkedOutBy: Option[User],
  weight: Float
  ) {

  def checkOut(who: User): Book = this.copy(checkedOutBy = Some(who))
  def checkIn: Book             = this.copy(checkedOutBy = None)

  /**
   * @return true if this book is checked out, false if it is not
   */
  def isCheckedOut: Boolean = checkedOutBy isDefined

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
