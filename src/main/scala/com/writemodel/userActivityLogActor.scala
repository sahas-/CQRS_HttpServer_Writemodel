package com.writemodel

import akka.actor._
import akka.persistence._
import scala.io.StdIn

case class eventStruct(timeStamp:Long, command: String, payload: String)
case class Command(data:eventStruct)
//case class Evt(data:String)

case object Shutdown
case class EventStore(events: List[String] = Nil){
  def update(data:String): EventStore = {
    copy(data::events)
  }
  def size: Int = events.length
  override def toString: String = events.reverse.toString
}
object UserActivityLogActor{
  final case class Command(data:String)
  def props: Props = Props[UserActivityLogActor]
}

class UserActivityLogActor extends PersistentActor with ActorLogging {
  var eventStore = EventStore()
  def persistenceId: String = "UserActivityLedger"
  def storeEvent(event: String) =
    eventStore = eventStore.update(event)
  def numEvents = eventStore.size-1

  /**
    * for logging
    */
  val actor: String = "UserActivityLogActor"
  override def preStart(): Unit = log.info(s"---${actor} started---")
  override def postStop(): Unit = log.info(s"---${actor} stopped---")

  /**
    * for recovery
    *
    * @return
    */
  override def receiveRecover: Receive = {
    case evt: String => {
      log.info(s"---in recovery mode, received event ${evt}---")
      storeEvent(evt)
      }
    case SnapshotOffer(_, snapshot: EventStore) => {
      log.info("recovering events from latest snapshot !!")
      eventStore = snapshot
    }
  }


  /**
    * for command processing
    *
    * @return
    */
  val snapShotInterval = 100
  val receiveCommand: Receive ={
    case Command(data) =>
      //you may decide whether to persist the command or do some business logic before peristing
      persist(s"${data}"){
          event=>storeEvent(event)
            context.system.eventStream.publish(event)
            if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
              saveSnapshot(eventStore)
            sender()!"ok"
        }
    case Shutdown => context.stop(self)
    case SaveSnapshotSuccess(metadata) => log.info("Snapshot saved successfully !!")
    case SaveSnapshotFailure(metadata,reason) => log.error(s"Failed saving snapshot, reason : ${reason}")
  }
}

