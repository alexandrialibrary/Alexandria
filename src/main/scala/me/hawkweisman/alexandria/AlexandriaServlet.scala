package me.hawkweisman.alexandria

import org.scalatra._
import scalate.ScalateSupport

class AlexandriaServlet extends AlexandriaStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
