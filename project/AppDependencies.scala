import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "8.4.0"
  val play = "30"

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-play-$play" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"         %% s"bootstrap-test-play-$play" % hmrcBootstrapVersion % Test
  )
}
