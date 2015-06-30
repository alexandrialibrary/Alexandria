package me.hawkweisman.alexandria

import org.fusesource.scalate.{ TemplateEngine, Binding }
import org.fusesource.scalate.layout.DefaultLayoutStrategy

import org.scalatra._
import scalate.ScalateSupport

import com.typesafe.scalalogging.LazyLogging

import javax.servlet.http.HttpServletRequest

import collection.mutable

import slick.driver.H2Driver.api._

trait AlexandriaStack extends ScalatraServlet
  with ScalateSupport
  with LazyLogging {

  val db: Database

  before() {
    response.addHeader("X-Clacks-Overhead","GNU Terry Pratchett")
  }

  /* wire up the pre-compiled templates */
  override protected def defaultTemplatePath: List[String] = List("/WEB-INF/templates/views")
  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      TemplateEngine.templateTypes.map("/WEB-INF/templates/layouts/default." + _): _*)
    engine.packagePrefix = "templates"
    engine
  }
  /* end wiring up the pre-compiled templates */

  override protected def templateAttributes(implicit request: HttpServletRequest): mutable.Map[String, Any] = {
    super.templateAttributes ++ mutable.Map.empty // Add extra attributes here, they need bindings in the build file
  }


  notFound { //TODO: custom 404 page
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

}
