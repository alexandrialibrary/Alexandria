package me.hawkweisman.alexandria
package model

case class Author(
  firstName: String,
  lastName: String,
  middleName: String
  )

case class Book (
    isbn: ISBN, // ISBNs are unique identifiers for a book in the database
    title: String,
    subtitle: Option[String],
    authors: Seq[Author],
    pages: Int,
    deweys: Seq[Float],
    checkedOut: Bool // todo: check out to a person
) {
  def checkOut: Book = Book(isbn,title,subtitle,authors,pages,deweys,true)
  def checkIn: Book = Book(isbn,title,subtitle,authors,pages,deweys,false)
}
