package com.writemodel

import akka.actor.{ActorRef,ActorSystem, Props}
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route


object HTTPServer extends App with Routes{
  // bootstrap server & dependencies
  implicit val system = ActorSystem("http-server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // server configs
  val host = "0.0.0.0"
  val port = 8080
  val route = Routes

  // start the server
  val serverBinding = Http().bindAndHandle(route,host,port)
  
  // handle server events
  serverBinding.onComplete{
    case scala.util.Success(bound) =>{
      println(s"Server is online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    }
    case scala.util.Failure(exception)=>{
      println(s"Failed starting the server")
      exception.printStackTrace()
      system.terminate()
    }
  }
}
