package me.hawkweisman.alexandria
package model

import java.sql.Date

import slick.driver.H2Driver.api._

/**
 * Definitions for Alexandria database tables
 *
 * Created by hawk on 5/20/15.
 */
object Tables {

  val books   = TableQuery[Books]
  val loans   = TableQuery[Loans]
  val users   = TableQuery[Users]
  val authors = TableQuery[Authors]
  val wrote   = TableQuery[Wrote]
  val deweys  = TableQuery[DeweyDecimals]

  /** DBIO Action which creates the schema */
  val createSchemaAction = (
    books.schema ++ loans.schema ++ users.schema ++ authors.schema ++ wrote.schema
  ).create

  /** DBIO Action which DESTROYS FUCKING EVERYTHING */
  val dropTablesAction = DBIO.seq(
    wrote.schema.drop,
    deweys.schema.drop,
    loans.schema.drop,
    books.schema.drop,
    users.schema.drop,
    authors.schema.drop)

  //TODO: AUTH table for password hashes

  /**
   * Look up the book matching a given ISBN
   * @param isbn the ISBN to look up
   * @return a DBIOAction containing a Future on the book matching that ISBN
   */
  val booksByISBN = for {
    isbn <- Parameters[String]
    book <- books if book.isbn === isbn
  } yield book

  val authorByName = for {
    (first,last) <- Parameters[(String,String)]
    author       <- authors
      if author.firstName === first && author.lastName === last
  } yield author

  val sortedAuthorsFirst = Compiled( (offset: ConstColumn[Long]) =>
    authors.sortBy(_.firstName.asc)
           .drop(offset)
          )

  val sortedAuthorsFirstCount = Compiled(
    (offset: ConstColumn[Long], count: ConstColumn[Long]) =>
      authors.sortBy(_.firstName.asc)
             .drop(offset)
             .take(count)
           )

  val sortedAuthorsLast = Compiled( (offset: ConstColumn[Long]) =>
    authors.sortBy(_.lastName.asc)
           .drop(offset)
         )

  val sortedAuthorsLastCount = Compiled(
    (offset: ConstColumn[Long], count: ConstColumn[Long]) =>
      authors.sortBy(_.lastName.asc)
             .drop(offset)
             .take(count)
           )

  val sortedBooksTitle = Compiled( (offset: ConstColumn[Long]) =>
     books.sortBy(_.title.replace("The ", "").asc)
          .drop(offset)
         )
  val sortedBooksTitleCount = Compiled(
    (offset: ConstColumn[Long], count: ConstColumn[Long]) =>
      books.sortBy(_.title.replace("The ", "").asc)
           .drop(offset)
           .take(count)
        )
  class Books(tag: Tag) extends Table[Book](tag, "BOOKS"){

    def isbn      = column[String]("ISBN", O.PrimaryKey)
    def title     = column[String]("TITLE")
    def subtitle  = column[Option[String]]("SUBTITLE")
    def byline    = column[String]("BYLINE")
    def publisher = column[String]("PUBLISHER")
    def published = column[String]("PUBLISHED")
    def pages     = column[Int]("PAGES")
    def weight    = column[Option[String]]("WEIGHT")

    def * = (isbn,title,subtitle,byline,pages,publisher,published,weight) <> (
      (Book.apply _ ).tupled, Book.unapply)

    def authors = wrote filter (_.bookISBN === isbn) flatMap (_.author)
    def deweyDecimals = deweys filter (_.isbn === isbn) map (_.dewey)
    def loanedTo = loans filter (_.isbn === isbn) flatMap (_.who)
    def loanedUntil = loans filter (_.isbn === isbn) map (_.until)
  }

  class Loans(tag: Tag) extends Table[(Int,String,Date)](tag, "LOANS") {
    def userID = column[Int]("USER_ID")
    def isbn   = column[String]("ISBN")
    def until  = column[Date]("UNTIL")

    def what = foreignKey("ISBN_FK", isbn, books)(b => b.isbn)
    def who  = foreignKey("USER_FK", userID, users)(u => u.id)

    def * = (userID,isbn,until)
  }

  class Users(tag: Tag) extends Table[User](tag, "USERS") {

    def id          = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName   = column[String]("FIRST_NAME")
    def middleName  = column[Option[String]]("MIDDLE_NAME")
    def lastName    = column[String]("LAST_NAME")
    def userName    = column[String]("USER_NAME")

    def * = (id,firstName,middleName,lastName,userName) <> (User.tupled, User.unapply)
  }

  class Authors(tag: Tag) extends Table[(Author)](tag,"AUTHORS") {

    def id          = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName   = column[String]("FIRST_NAME")
    def middleName  = column[Option[String]]("MIDDLE_NAME")
    def lastName    = column[String]("LAST_NAME")

    def * = (firstName,middleName,lastName) <> ((Author.apply _).tupled, Author.unapply)

    def books = wrote filter (_.authorID === id) flatMap (_.book)

    def fullName = index("FULL_NAME", (firstName, middleName, lastName), unique = true)

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
