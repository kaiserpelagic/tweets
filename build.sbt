scalaVersion in Global := "2.11.8"

lazy val root = project.in(file("."))

mainClass in Compile := Some("tweets.Main")

resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)

val http4sV = "0.16.5a"

val scalazV = "7.2.16"

libraryDependencies ++= Seq(
  "org.scalaz"  %% "scalaz-core"         % scalazV,
  "io.argonaut" %% "argonaut-jawn"       % "6.2",
  "org.http4s"  %% "http4s-blaze-server" % http4sV,
  "org.http4s"  %% "http4s-blaze-client" % http4sV,
  "org.http4s"  %% "http4s-argonaut"     % http4sV,
  "org.http4s"  %% "http4s-dsl"          % http4sV,
  "org.http4s"  %% "http4s-client"       % http4sV,
  "org.specs2"  %% "specs2-core"         % "4.0.0" % "test",
  "org.scalacheck" %% "scalacheck"       % "1.13.5" % "test",
  "org.scalaz"  %% "scalaz-scalacheck-binding" % scalazV % "test"
)
