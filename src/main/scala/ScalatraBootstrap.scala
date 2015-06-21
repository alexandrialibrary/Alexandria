import me.hawkweisman.alexandria._
import me.hawkweisman.alexandria.controllers._
import me.hawkweisman.alexandria.controllers.swagger._

import org.scalatra._
import javax.servlet.ServletContext

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.JdbcDriver.api._

class ScalatraBootstrap extends LifeCycle {

  val cpds = new ComboPooledDataSource
  //logger.info("Created c3p0 connection pool")

  implicit val swagger = new AlexandriaSwagger

  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds)     // create a Database which uses the DataSource
    // mount the admin controller
    context mount (AdminController(db), "/admin/*")
    // mount the API swagger control
    context mount (new ResourcesController, "/api/docs")
    // mount the API controller
    context mount (APIController(db), "/api/*")
    // mount the application
    context mount (AppController(db), "/*") // TODO: eventually this won't require DB access
  }

  private def closeDbConnection() {
    //logger.info("Closing c3po connection pool")
    cpds.close
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }

}
