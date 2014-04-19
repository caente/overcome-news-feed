package com.dimeder.maintenance

import akka.actor.Actor
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import com.dimeder.models.{WordsModel, WordsCleaner}
import akka.pattern.pipe
import com.dimeder.models.WordsModel.Delete
import akka.event.LoggingReceive
import com.dimeder.log
/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */

object Cleaner {

  case object Clean

  case class Source(_id: Long)

  case class Count(source: Long, count: Int)


}

class Cleaner(wordsCleaner:WordsCleaner) extends Actor {

  import context.dispatcher

  val config = ConfigFactory.load()

  val min_length = config.getInt("cleaner.min")

  override def preStart(): Unit = {
    context.system.scheduler.schedule(0 minutes, 10 minutes, self, Cleaner.Clean)
  }

  def receive = LoggingReceive {
    case Cleaner.Clean => wordsCleaner.getSources.subscribe(source => self ! Cleaner.Source(source))
    case Cleaner.Source(source) =>
      wordsCleaner.countWordsBySource(source).map(count => Cleaner.Count(source, count)) pipeTo self
    case Cleaner.Count(source, count) =>
      log.debug(s"total count: $count")
      if (count > min_length)
        wordsCleaner.removeOldestWords(Delete(source, count - min_length))
  }

}
