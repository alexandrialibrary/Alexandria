import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object AlexandriaBuild extends Build {

  val projOrganization = "me.hawkweisman"
  val projName = "alexandria"
  val projVersion = "0.0.1"
  val projScalaVersion = "2.11.7"
  val scalatraVersion = "2.4.0.RC1"
  val slickVersion = "3.0.0"

  lazy val project = Project (
    "alexandria",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := projOrganization,
      name := projName,
      scalaVersion := projScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      resolvers += "Hawk's Bintray Repo" at "https://dl.bintray.com/hawkw/maven",
      libraryDependencies ++= Seq(
        "org.scalatra"                %%  "scalatra"            % scalatraVersion,
        "org.scalatra"                %%  "scalatra-scalate"    % scalatraVersion,
        "org.scalatra"                %%  "scalatra-json"       % scalatraVersion,
        "org.scalatra"                %%  "scalatra-auth"       % scalatraVersion,
        "org.scalatra"                %%  "scalatra-swagger"    % scalatraVersion,
        "org.scalatra"                %%  "scalatra-scalatest"  % scalatraVersion % "test",
        "org.scalacheck"              %%  "scalacheck"          % "1.12.2"        % "test",
        "org.scalatest"               %%  "scalatest"           % "2.2.4"         % "test",
        "org.json4s"                  %%  "json4s-native"       % "3.3.0.RC1",
        "net.databinder.dispatch"     %%  "dispatch-core"       % "0.11.2",
        "me.hawkweisman"              %%  "util"                % "0.1.1",
        "com.typesafe.slick"          %%  "slick"               % slickVersion,
        "com.typesafe.scala-logging"  %%  "scala-logging"       % "3.1.0",
        "com.h2database"              %   "h2"                  % "1.3.166",
        "c3p0"                        %   "c3p0"                % "0.9.1.2",
        "ch.qos.logback"              %   "logback-classic"     % "1.1.2" % "runtime",
        "org.eclipse.jetty"           %   "jetty-webapp"        % "9.1.5.v20140505" % "container",
        "org.eclipse.jetty"           %   "jetty-plus"          % "9.1.5.v20140505" % "container",
        "javax.servlet"               %   "javax.servlet-api"   % "3.1.0"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext",
                importMembers = true,
                isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )

}
