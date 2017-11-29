scalaVersion in Global := "2.11.8"

lazy val root = project.in(file("."))

resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
