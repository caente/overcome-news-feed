package com.dimeder.exractors

import scala.io.Source
import com.dimeder.streams.TwitterStreamer.Tweet
import scala.concurrent.Future
import com.dimeder.log
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */
object TwitterExtractor {

  trait Item {

    def twitter_id: Long

    def count: Int

    def item: String
  }

  case class Sample(tweet: Tweet, items: Set[Item])

  trait Link extends Item

  case class Word(twitter_id: Long, item: String, count: Int) extends Item


  trait HashTag extends Item

  trait TwitterUser extends Item


  val commonWords = Source.fromURL(getClass.getResource("/words.txt")).getLines().toSet

  val linkRegex = """((mailto\:|(news|(ht|f)tp(s?))\://){1}\S+)"""

  val linkPattern = linkRegex.r

  def extractWords(tweet: Tweet): Future[Sample] = Future {
    def tag(word: String, count: Int): Word = word match {
      case w if w.startsWith("#") => new Word(tweet.origin, word, count) with HashTag
      case w if w.startsWith("@") => new Word(tweet.origin, word, count) with TwitterUser
      case _ => Word(tweet.origin, word, count)
    }
    log.debug(s"extracting $tweet")
    val links = linkPattern.findAllIn(tweet.text).toList.groupBy(l => l).map {
      case (l, ls) => new Word(tweet.origin, l, ls.size) with Link
    }.toSet
    val items = tweet.text
      .replaceAll(linkRegex, "")
      .split("[,\\.\" \"]")
      .filter(w => w.length > 0)
      .filterNot(w => commonWords.exists(_.toLowerCase.trim == w.toLowerCase.trim))
      .groupBy(w => w)
      .map {
      case (w, ws) => tag(w.toUpperCase, ws.size)
    }
    Sample(tweet, items.toSet ++ links)
  }

}
