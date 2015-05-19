package me.hawkweisman.alexandria
package model

import scala.util.Sorting

case class Person(
  firstName: String,
  lastName: String,
  middleName: String)

object FirstNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.firstName compare b.firstName
}

object LastNameOrdering extends Ordering[Person] {
  def compare(a: Person, b: Person) = a.firstName compare b.firstName
}
