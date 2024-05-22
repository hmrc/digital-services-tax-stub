resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.21.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "2.5.0")

addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.2")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
