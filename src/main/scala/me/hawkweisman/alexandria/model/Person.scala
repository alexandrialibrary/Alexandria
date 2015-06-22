package me.hawkweisman.alexandria
package model

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
  def getFirstName = this.firstName
  def getMiddleName = this.middleName
  def getLastName = this.lastName
  val name = s"$firstName ${middleName map { _ + " " } getOrElse ""}$lastName"
}

object Author {
  def apply(first: String, middle: Option[String], last: String): Author =
    Author(first,middle,last)
  def unapply(a: Author): Option[(String,Option[String],String)] =
    Some((a.getFirstName, a.getMiddleName, a.getLastName))
}

case class User(
  id: Int,
  getFirstName: String,
  getMiddleName: Option[String],
  getLastName: String,
  username: String
  ) extends Person
