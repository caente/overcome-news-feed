package com.dimeder

import com.dimeder.streams.TwitterStreamer
import com.dimeder.models.{WordsModel, TwitterModel}
import com.dimeder.exractors.TwitterExtractor
import akka.actor.{Props, ActorSystem}
import com.dimeder.maintenance.Cleaner

/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */
object Main extends App {

  val system = ActorSystem("tweet-feed")
  system.actorOf(Props(new Cleaner))

  TwitterModel.getTwitterAccount(1407921984) map {
    twitterCredentials =>
      TwitterStreamer(twitterCredentials).subscribe {
        tweet =>
          val sample = TwitterExtractor.extractWords(tweet)
          WordsModel.upsertWord(sample)
      }
  }


}
