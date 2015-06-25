package me.hawkweisman.alexandria
package controllers
package responses

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

import model.Book

/**
 * Custom serializer for serializing [[model.Book Book]]s which
 * transforms `None`s to `null`s instead of removing them.
 *
 * @author Hawk Weisman
 *
 * Created by hawk on 6/23/15.
 */
object BookSerializer extends CustomSerializer[Book](implicit format => ({
    case json: JValue => Book(
      isbn = (json \ "isbn").extract[String],
      title = (json \ "title").extract[String],
      subtitle = (json \ "subtitle").toOption map {
        _.extract[String]
      },
      byline = (json \ "byline").extract[String],
      pages = (json \ "pages").extract[Int],
      publisher = (json \ "publisher").extract[String],
      published_date = (json \ "published_date").extract[String],
      weight = (json \ "weight").toOption map {
        _.extract[String]
      }
    )
  }, {
    case book: Book =>
      ("isbn" -> book.isbn) ~
        ("title" -> book.title) ~
        ("subtitle" -> book.subtitle.orNull) ~
        ("byline" -> book.byline) ~
        ("pages" -> book.pages) ~
        ("publisher" -> book.publisher) ~
        ("published_date" -> book.published_date) ~
        ("weight" -> book.weight.orNull)
  })
)
