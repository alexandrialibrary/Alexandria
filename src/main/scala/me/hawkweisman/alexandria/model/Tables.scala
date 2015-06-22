package me.hawkweisman.alexandria
package model

import java.sql.Date


import slick.driver.JdbcDriver.api._

/**
 * Created by hawk on 5/20/15.
 */
object Tables {

  val books   = TableQuery[Books]
  val loans   = TableQuery[Loans]
  val users   = TableQuery[Users]
  val authors = TableQuery[Authors]
  val wrote   = TableQuery[Wrote]
  val deweys  = TableQuery[DeweyDecimals]

  // DBIO Action which creates the schema
  val createSchemaAction = (
    books.schema ++ loans.schema ++ users.schema ++ authors.schema ++ wrote.schema
  ).create

  //TODO: AUTH table for password hashes

  class Books(tag: Tag) extends Table[(String,String,Option[String],Int,String,String,String,Option[Int])](tag, "BOOKS"){

    def isbn = column[String]("ISBN", O.PrimaryKey)
    def title = column[String]("TITLE")
    def subtitle = column[Option[String]]("SUBTITLE")
    def publisher = column[String]("PUBLISHER")
    def published = column[String]("PUBLISHED")
    def pages = column[Int]("PAGES")
    def weight = column[String]("WEIGHT")
    def ownerID = column[Option[Int]]("OWNER_ID")

    def owner = foreignKey("OWNER_FK", ownerID, users)(_.id)

    def * = (isbn,title,subtitle,pages,publisher,published,weight,ownerID)

    def authors = wrote filter (_.bookISBN === isbn) flatMap (_.author)
    def deweyDecimals = deweys filter (_.isbn === isbn) map (_.dewey)
    def loanedTo = loans filter (_.isbn === isbn) flatMap (_.who)
    def loanedUntil = loans filter (_.isbn === isbn) map (_.until)
  }

  class Loans(tag: Tag) extends Table[(Int,String,Date)](tag, "LOANS") {
    def userID = column[Int]("USER_ID")
    def isbn = column[String]("ISBN")
    def until = column[Date]("UNTIL")

    def what = foreignKey("ISBN_FK", isbn, books)(b => b.isbn)
    def who  = foreignKey("USER_FK", userID, users)(u => u.id)

    def * = (userID,isbn,until)
  }

  class Users(tag: Tag) extends Table[User](tag, "USERS") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[String]("LAST_NAME")
    def userName = column[String]("USER_NAME")

    def * = (id,firstName,middleName,lastName,userName) <> (User.tupled, User.unapply)
  }

  class Authors(tag: Tag) extends Table[(Author)](tag,"AUTHORS") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[String]("LAST_NAME")

    def * = (firstName,middleName,lastName) <> ((Author.apply _).tupled, Author.unapply)

    def books = wrote filter (_.authorID === id) flatMap (_.book)

  }

  class DeweyDecimals(tag:Tag) extends Table[(String,String)](tag, "DEWEYS") {
    def isbn = column[String]("ISBN")
    def dewey = column[String]("DEWEY")

    def * = (isbn, dewey)

    def book = foreignKey("BOOK", isbn, books)(b => b.isbn)
  }

  class Wrote(tag: Tag) extends Table[(Int,String)](tag, "WROTE") {
    def authorID = column[Int]("AUTHOR_ID")
    def bookISBN = column[String]("ISBN")

    def author   = foreignKey("AUTHOR", authorID, authors)(a => a.id)
    def book     = foreignKey("BOOK", bookISBN, books)(b => b.isbn)

    def * = authorID -> bookISBN
  }

}
