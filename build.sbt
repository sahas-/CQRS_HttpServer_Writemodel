organization := "com.writemodel"
name := "cqrs-http-server"
version := "1.0"
scalaVersion := "2.12.4"

packageName in Docker := "cqrs-http-server"
dockerExposedPorts := Seq(8080)


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.5",
  "com.typesafe.akka"%% "akka-slf4j" % "2.5.18",
  "com.typesafe.akka"%% "akka-actor" % "2.5.18",
  "com.typesafe.akka"%% "akka-persistence"  % "2.5.18",
  "com.typesafe.akka"%%"akka-stream"%"2.5.18",
  "com.safety-data" %% "akka-persistence-redis" % "0.4.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9"
)

unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/resources"
}
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

mainClass in Compile := Some("com.writemodel.HTTPServer")
dockerBaseImage := "openjdk:jre-alpine"