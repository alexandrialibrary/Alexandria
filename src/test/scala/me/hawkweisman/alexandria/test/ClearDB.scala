package me.hawkweisman.alexandria.test

import me.hawkweisman.alexandria.model.Tables._

import org.scalatest._
import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration._

trait ClearDB extends BeforeAndAfterEach { this: Suite =>

  def db: Database

  override def beforeEach() {
    // ensure that there's a fresh DB created before running each test
    Await.ready(db run authors.schema.create, Duration.Inf)
    Await.ready(db run books.schema.create, Duration.Inf)
    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    // ensure any data left over from running tests is deleted
    finally {
        Await.ready(db run authors.schema.drop, Duration.Inf)
        Await.ready(db run books.schema.drop, Duration.Inf)
    }
  }

}
