package me.hawkweisman.alexandria
package model

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Sorting

trait Person {
  def getFirstName: String
  def getMiddleName: Option[String]
  def getLastName: String
}
object FirstNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.getFirstName compare b.getFirstName
}

object LastNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.getLastName compare b.getLastName
}

class Author(
  private val firstName: String,
  private val middleName: Option[String],
  private val lastName: String
  ) extends Person {

  def this(first: String, middle: String, last: String) = this(first,Some(middle),last)
  def this(first: String, last: String) = this(first,None,last)

  def getFirstName = this.firstName
  def getMiddleName = this.middleName
  def getLastName = this.lastName
  val name = s"$firstName ${middleName map { _ + " " } getOrElse ""}$lastName"

  override def toString: String = name

  override def equals(other: Any): Boolean = other match {
    case Author(first, middle, last) => first == firstName && middle == middleName && last == lastName
    case _                           => false
  }
}

object Author {

  private implicit val formats = DefaultFormats
  def apply(first: String, middle: Option[String], last: String): Author =
    new Author(first,middle,last)
  def unapply(a: Author): Option[(String,Option[String],String)] =
    Some((a.getFirstName, a.getMiddleName, a.getLastName))

  def fromJson(json: JValue): List[Author] = {
    val JArray(authorList) = json \\ "authors"
    authorList map { value: JValue =>
      (value \\ "name").extract[String] split " " match {
        case Array(first,middle,last) => Author(first,Some(middle),last)
        case Array(first,last)        => Author(first,None,last)
        case _                        => ??? // handle more complex cases
      }
    }
  }
}

final case class User(
  id: Int,
  getFirstName: String,
  getMiddleName: Option[String],
  getLastName: String,
  username: String
  ) extends Person
