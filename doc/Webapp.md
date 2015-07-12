Writing the Web App
-------------------

### Web Assets Pipeline

Currently, we have the Web Resource Optimizer for Java (wro4j) configured to manage our web assets pipeline.

All code for the web app goes in `/src/main/webapp`. CoffeeScript goes in the `coffee/` directory. SASS and CSS go in `styles/`, while YavaScript goes in `js/`. All web resources will be compiled into the `target/wro4j/compiled` directory (which eventually will be added to the web jar we will eventually publish).

Currently, code in the file `alexandria-app.coffee` will be compiled into the `alexandria-app.js` file, which will be the Angular controller for the main app, while `alexandria-admin.coffee` will be compiled into the `alexandria-admin.js` controller for the admin page app. If we eventually want to have multiple files containing the code for the Angular controllers, we can change this to use separate directories for the app and the admin app controllers.

All scripts in `js/` are compiled and minified into the `scripts.js` file, which will include all the scripts that will be used by both the main app and the admin app. All stylesheets are compiled and minified into `styles.css`, which should be used the same.

To have the WRO4j automatically compile web-app sources when you make changes, use the SBT command `~wro4j`. The `~` indicates that SBT should watch the source directory and re-run the `wro4j` command when the sources change. WRO4J will automatically be run when `compile` is invoked, as well.


### Templates

I've written quick SSP templates to get started on the frontend. The layout `layouts/default.ssp` links in all the scripts we use and takes one variable, `appScript`, that sets the Angular controller for the page being rendered (either `alexandria-app` or `alexandria-admin`). Views are rendered using this layout by default.
