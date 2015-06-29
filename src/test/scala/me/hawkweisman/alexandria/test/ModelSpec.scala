package me.hawkweisman.alexandria
package test

import me.hawkweisman.alexandria.model.{LastNameOrdering, Person, FirstNameOrdering, Author}

import org.scalatest.{Matchers, WordSpec}

import scala.util.Sorting

/**
 * Created by hawk on 6/29/15.
 */
class ModelSpec extends WordSpec
  with Matchers{

  "The Author model" when {
    "sorted by first name" should {
      "be in alphabetical order" in {
        val authors: Array[Person] = Array(
          new Author("Clive", "Barker"),
          new Author("Agatha", "Christie"),
          new Author("William", "Shakespeare"),
          new Author("Jane", "Austen"),
          new Author("Charles", "Beaudelaire"),
          new Author("Ernest", "Hemingway"),
          new Author("Donald", "E.", "Knuth"),
          new Author("Umberto", "Ecco"),
          new Author("Andrew", "Tanenbaum")
        )
        Sorting.quickSort(authors)(FirstNameOrdering)
        authors should contain theSameElementsInOrderAs Seq(
          new Author("Agatha", "Christie"),
          new Author("Andrew", "Tanenbaum"),
          new Author("Charles", "Beaudelaire"),
          new Author("Clive", "Barker"),
          new Author("Donald", "E.", "Knuth"),
          new Author("Ernest", "Hemingway"),
          new Author("Jane", "Austen"),
          new Author("Umberto", "Ecco"),
          new Author("William", "Shakespeare")
          )
      }
    }
    "sorted by last name" should {
      "be in alphabetical order" in {
        val authors: Array[Person] = Array(
          new Author("Clive", "Barker"),
          new Author("Agatha", "Christie"),
          new Author("William", "Shakespeare"),
          new Author("Jane", "Austen"),
          new Author("Charles", "Beaudelaire"),
          new Author("Ernest", "Hemingway"),
          new Author("Donald", "E.", "Knuth"),
          new Author("Umberto", "Ecco"),
          new Author("Andrew", "Tanenbaum")
        )
        Sorting.quickSort(authors)(LastNameOrdering)
        authors should contain theSameElementsInOrderAs Seq(
          new Author("Jane", "Austen"),
          new Author("Clive", "Barker"),
          new Author("Charles", "Beaudelaire"),
          new Author("Agatha", "Christie"),
          new Author("Umberto", "Ecco"),
          new Author("Ernest", "Hemingway"),
          new Author("Donald", "E.", "Knuth"),
          new Author("William", "Shakespeare"),
          new Author("Andrew", "Tanenbaum")
        )
      }
    }
  }

}
