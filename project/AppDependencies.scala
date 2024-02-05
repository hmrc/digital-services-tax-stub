import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "8.4.0"
  val play = "30"

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-play-$play" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"         %% "stub-data-generator"    % "1.1.0"              % Test,
    "org.scalatest"          %% "scalatest"                   % "3.2.17" % Test,
    "uk.gov.hmrc"         %% s"bootstrap-test-play-$play" % hmrcBootstrapVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all"           % "0.64.6"             % Test,
    "org.pegdown"          % "pegdown"                % "1.6.0"              % Test,
    "org.scalatestplus"   %% "mockito-3-12"           % "3.2.10.0"           % Test
  )

}
