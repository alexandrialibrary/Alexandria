package me.hawkweisman.alexandria
package model

import scala.util.Sorting

trait Person {
  def firstName: String
  def middleName: Option[String]
  def lastName: String
}
object FirstNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.firstName compare b.firstName
}

object LastNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.firstName compare b.firstName
}

case class Author(
  firstName: String,
  middleName: Option[String],
  lastName: String
  ) extends Person

case class User(
  id: Int,
  firstName: String,
  middleName: Option[String],
  lastName: String,
  username: String
  ) extends Person
