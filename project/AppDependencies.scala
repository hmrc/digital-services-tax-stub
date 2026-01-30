import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "10.5.0"
  val play = "30"

  val compile = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-backend-play-$play" % hmrcBootstrapVersion,
    "uk.gov.hmrc"       %% "stub-data-generator"           % "1.6.0",
    "io.chrisdavenport" %% "cats-scalacheck"               % "0.3.2"
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-play-$play"    % hmrcBootstrapVersion % Test
  )
}
