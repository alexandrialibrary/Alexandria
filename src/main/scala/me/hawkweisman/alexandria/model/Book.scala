package me.hawkweisman.alexandria
package model

case class Book (
    isbn: Long, // ISBNs are unique identifiers for a book in the database
    title: String,
    author: String
)
