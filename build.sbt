val appName = "digital-services-tax-stub"
PlayKeys.playDefaultPort := 8742

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala,SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=unused-imports&src=html/.*:s",
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s"
  )

scalaVersion := "3.7.4"
