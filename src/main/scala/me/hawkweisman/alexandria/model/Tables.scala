package me.hawkweisman.alexandria
package model

import java.sql.Date

import scala.slick.driver.H2Driver.simple._

/**
 * Created by hawk on 5/20/15.
 */
object Tables {

  val books = TableQuery[Books]
  val loans = TableQuery[Loans]
  val users = TableQuery[Users]

  //TODO: AUTHORS table
  //TODO: WROTE relation for associating author -> book
  //TODO: AUTH table for password hashes

  class Books(tag: Tag) extends Table[
    (String,String,Option[String],String,Date,Int,Double,Option[Int])
    ](tag, "BOOKS"){

    def isbn = column[String]("ISBN", O.PrimaryKey)
    def title = column[String]("TITLE")
    def subtitle = column[Option[String]]("SUBTITLE")
    def publisher = column[String]("PUBLISHER")
    def published = column[Date]("PUBLISHED")
    def pages = column[Int]("PAGES")
    def weight = column[Double]("WEIGHT")
    def owner = column[Option[Int]]("OWNER_ID")

    def owner_id = foreignKey("OWNER_FK", owner, users)(_.id)

    def * = (isbn,title,subtitle,publisher,published,pages,weight,owner)
  }

  class Loans(tag: Tag) extends Table[(Int,String,Date)](tag, "LOANS") {
    def user = column[Int]("USER_ID")
    def isbn = column[String]("ISBN")
    def until = column[Date]("UNTIL")

    def what = foreignKey("ISBN_FK", isbn, books)(_.isbn)

    def * = (user,isbn,until)
  }

  class Users(tag: Tag) extends Table[User](tag, "USERS") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[String]("LAST_NAME")
    def userName = column[String]("USER_NAME")

    def * = (id,firstName,middleName,lastName,userName) <> (User.tupled, User.unapply)
  }

}
