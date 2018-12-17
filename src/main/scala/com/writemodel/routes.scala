package com.writemodel

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import akka.util.Timeout
import spray.json._
import com.writemodel.UserActivityLogActor._

final case class User(name: String, id: String)
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}

trait Routes extends JsonSupport {
  implicit def system: ActorSystem
  // val log = Logging(system, classOf[Routes])
  lazy val userActivityLogger = system.actorOf(Props[UserActivityLogActor])
  implicit val timeout = Timeout(60.seconds)
  //Routes
  val Routes =
    path("") {
      get {
        complete(StatusCodes.OK)
      } ~
        post {
          entity(as[User]) { User =>
            complete(StatusCodes.OK,s"Hi ${User.name}")
          }
        }
    } ~
      path("user-joined" / Segment) { id =>
        {
          post {
            onSuccess(
              userActivityLogger ? Command(
                eventStruct(System.currentTimeMillis / 1000,
                            "user-joined",
                            s"id:${id}"))) {
              case "ok" => complete(StatusCodes.OK, s"User ${id} joined")
              case _    => complete(StatusCodes.InternalServerError)
            }

          }
        }
      } ~
      path("user-left" / Segment) { id =>
        {
          post {
            onSuccess(
              userActivityLogger ? Command(
                eventStruct(System.currentTimeMillis / 1000,
                            "user-left",
                            s"id:${id}"))) {
              case "ok" => complete(StatusCodes.OK, s"User ${id} left")
              case _    => complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
}
