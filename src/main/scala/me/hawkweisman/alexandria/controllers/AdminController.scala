package me.hawkweisman.alexandria
package controllers

import model.Tables._

import org.scalatra._
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}

import scalate.ScalateSupport

import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Control for the Alexandria admin app.
 *
 * This gets attached at `/admin/` and handles the admin API and serving
 * the admin app page.
 *
 * @author Hawk Weisman
 */
case class AdminController(db: Database) extends AlexandriaStack
  with NativeJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  get("/"){
    logger info "handling request for Admin app"
    contentType = "text/html"
    <html><body>
    <h1>Eventually this will be the admin page.</h1>
    </body></html>
  }

  //////////////////////////////////////////////////////////////////////
  // REMOVE BEFORE FLIGHT //////////////////////////////////////////////
  // This route is for TESTING PURPOSES ONLY ///////////////////////////
  get("/db/create-tables") {
    logger info "got create tables request"
    Await.ready(db.run(createSchemaAction), Duration.Inf)
    logger info "created tables"
    contentType = "text/html"
    <html><body>
    <h1>created tables</h1>
    </body></html>
  } // REMOVE BEFORE FLIGHT ////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////

}
