import sbt.*

object AppDependencies {

  val hmrcBootstrapVersion = "10.7.0"
  val play = "30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-play-$play" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.6.0",
    "io.chrisdavenport" %% "cats-scalacheck"           % "0.3.2"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% s"bootstrap-test-play-$play" % hmrcBootstrapVersion % Test,
    "org.scalatestplus"      %% "scalacheck-1-17"              % "3.2.18.0",
  )
}
