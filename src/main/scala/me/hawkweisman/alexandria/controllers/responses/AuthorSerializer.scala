package me.hawkweisman.alexandria
package controllers
package responses

import model.Author
import org.json4s._
import org.json4s.JsonDSL._

/**
 * Custom serializer for [[model.Author Authors]].
 *
 * Serializes our [[model.Author Author]] model, which stores a first, middle, and last name
 * as separate strings to an Author JSON object with a single field storing the whole name, and
 * deserializes that object to our [[model.Author Author]] model.
 *
 * @author Hawk Weisman
 * @since v0.1.0
 *
 * @see [[model.Author]]
 * @see [[org.json4s.CustomSerializer]]
 *
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
