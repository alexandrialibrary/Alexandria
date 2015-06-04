package me.hawkweisman.alexandria

import org.scalatra._
import scalate.ScalateSupport

class AlexandriaServlet extends AlexandriaStack {

  get("/") {
    contentType = "text/html"
    jade(
      "main",
      "books" -> ???
    )
  }
  
}
