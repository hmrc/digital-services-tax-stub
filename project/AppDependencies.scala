import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "7.19.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"         %% "stub-data-generator"    % "1.1.0"              % Test,
    "uk.gov.hmrc"         %% "bootstrap-test-play-28" % hmrcBootstrapVersion % Test,
    "com.typesafe.play"   %% "play-test"              % current              % Test,
    "com.vladsch.flexmark" % "flexmark-all"           % "0.64.6"             % Test,
    "org.pegdown"          % "pegdown"                % "1.6.0"              % Test,
    "org.scalatestplus"   %% "mockito-3-12"           % "3.2.10.0"           % Test
  )

}
