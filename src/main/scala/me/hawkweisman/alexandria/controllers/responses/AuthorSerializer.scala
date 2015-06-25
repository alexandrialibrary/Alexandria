package me.hawkweisman.alexandria
package controllers
package responses

import model.Author
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

/**
 * Created by hawk on 6/24/15.
 */
object AuthorSerializer extends CustomSerializer[Author](implicit format => ({
  case json: JValue => (json \ "name").extract[String] split " " match {
    case Array(first,middle,last) => Author(first,Some(middle),last)
    case Array(first,last)        => Author(first,None,last)
    case _                        => ??? // handle more complex cases
  }
}, {
  case author: Author => "name" -> author.name
})
)
