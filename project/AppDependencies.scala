import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "10.1.0"
  val play = "30"

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-play-$play" % hmrcBootstrapVersion, // cannot update without moving to Scala 3
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.4.0"
  )

  val test = Seq(
    "uk.gov.hmrc"         %% s"bootstrap-test-play-$play" % hmrcBootstrapVersion % Test
  )
}
