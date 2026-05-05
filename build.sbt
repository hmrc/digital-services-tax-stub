val appName = "digital-services-tax-stub"
PlayKeys.playDefaultPort := 8742

scalaVersion := "3.7.1"
scalacOptions ++= Seq( "-feature", "-Wconf:src=.*routes.*:s", "-Wconf:msg=Flag.*repeatedly:s")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala,SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )


