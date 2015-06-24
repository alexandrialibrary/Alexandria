package me.hawkweisman.alexandria
package controllers
package responses

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

import model.Book

/**
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
      publishedDate = (json \ "publishedDate").extract[String],
      weight = (json \ "weight").toOption map {
        _.extract[String]
      }
    )
  }, {
    case Book(isbn,title,subtitle,byline,pages,publisher,publishedDate,weight) =>
      ("isbn" -> isbn) ~
        ("title" -> title) ~
        ("subtitle" -> subtitle.getOrElse(null)) ~
        ("byline" -> byline) ~
        ("pages" -> pages) ~
        ("publisher" -> publisher) ~
        ("publishedDate" -> publishedDate) ~
        ("weight" -> weight.getOrElse(null))
  })
)