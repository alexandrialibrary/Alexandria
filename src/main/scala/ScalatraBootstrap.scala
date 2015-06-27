import me.hawkweisman.alexandria.controllers._
import me.hawkweisman.alexandria.controllers.swagger._
import me.hawkweisman.alexandria.model.Tables._

import org.scalatra._
import javax.servlet.ServletContext

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

/**
 * Scalatra bootstrap class for Alexandria
 *
 * @author Hawk Weisman
 * @since v0.1.0
 */
class ScalatraBootstrap extends LifeCycle
  with LazyLogging {

  val cpds = new ComboPooledDataSource
  logger info "Created c3p0 connection pool"

  implicit val swagger = new AlexandriaSwagger
  logger info "Created Swagger API data"

  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds)     // create a Database which uses the DataSource
    logger info "Created database"
    // create tables if they don't already exist
    if (Await.result(db.run(MTable.getTables), Duration.Inf).toList.isEmpty) {
      Await.ready(db run createSchemaAction, Duration.Inf)
      logger info "Created table schemas"
    }
    // mount the admin controller
    context mount (AdminController(db), "/admin/*")
    logger info "Mounted AdminController at /admin/"
    // mount the API swagger control
    context mount (new ResourcesController, "/api/docs")
    logger info "Mounted ResourcesController at /api/docs"
    // mount the API controller
    context mount (APIController(db), "/api/*")
    logger info "Mounted APIController at /api/"
    // mount the application
    context mount (AppController(db), "/*") // TODO: eventually this won't require DB access
    logger info "Mounted AppController at /"
  }

  private def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close()
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection()
  }

}
