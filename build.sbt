val appName = "digital-services-tax-stub"
PlayKeys.playDefaultPort := 8742

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala,SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )

scalaVersion := "2.13.16"
