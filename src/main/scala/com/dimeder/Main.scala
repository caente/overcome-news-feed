package com.dimeder

import com.dimeder.streams.TwitterStreamer
import com.dimeder.models.{WordsModel, TwitterModel}
import com.dimeder.exractors.TwitterExtractor
import akka.actor.{Props, ActorSystem}
import com.dimeder.maintenance.Cleaner
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */
object Main extends App {

  val system = ActorSystem("tweet-feed")
  system.actorOf(Props(new Cleaner(WordsModel)))

  val config = ConfigFactory.load()

  TwitterModel.getTwitterAccount(config.getLong("sources.account")) map {
    twitterCredentials =>
      TwitterStreamer(twitterCredentials).subscribe {
        tweet =>
          TwitterExtractor.extractWords(tweet).map(WordsModel.upsertSample)
      }
  }


}
