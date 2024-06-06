val appName = "digital-services-tax-stub"
PlayKeys.playDefaultPort := 8742

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(resolvers += Resolver.jcenterRepo)

scalaVersion := "2.13.13"
