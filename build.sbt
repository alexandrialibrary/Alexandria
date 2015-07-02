import com.bowlingx.sbt.plugins.Wro4jPlugin._
import Wro4jKeys._

// import task settings
seq(wro4jSettings: _*)

// If you use xsbt-web-plugin, this will add compiled files to your war file:
(webappResources in Compile) <+= (targetFolder in generateResources in Compile)

lazy val gitHeadCommitSha = settingKey[String]("current git commit short SHA")

gitHeadCommitSha in ThisBuild := Process("git rev-parse --short HEAD").lines.head

version in ThisBuild := s"$projVersion-${gitHeadCommitSha.value}"

// Create a default Scala style task to run with tests
lazy val testScalastyle = taskKey[Unit]("testScalastyle")

testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value

(sbt.Keys.test in Test) <<= (sbt.Keys.test in Test) dependsOn testScalastyle
