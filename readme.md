### Setup
This repo uses akka-persistence and redis as a journal/snapshot persistence provider

Make sure to update your redis config in src/main/resources/applicaiton.conf
### compile
`sbt compile`
### run
`sbt run`
### create container
`sbt docker:publishLocal`
### run container
`docker run --rm -d -p8080:8080 cqrs-http-server:1.0`
