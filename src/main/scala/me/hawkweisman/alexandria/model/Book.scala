package me.hawkweisman.alexandria
package model

case class Book (
    isbn: ISBN, // ISBNs are unique identifiers for a book in the database
    title: String,
    authors: Seq[String],
)
