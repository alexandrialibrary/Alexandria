package me.hawkweisman.alexandria
package controllers
package responses

case class Book(
  isbn: String,
  title: String,
  subtitle: String,
  authors: Seq[String],
  dewey_decimal_numbers: Seq[String],
  pages: Int,
  publisher: String,
  published_date: String,
  weight: String
  )
