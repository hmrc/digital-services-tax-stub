import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "7.11.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28" % hmrcBootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % hmrcBootstrapVersion    % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8"                % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "stub-data-generator"       % "0.5.3"
  )

}
