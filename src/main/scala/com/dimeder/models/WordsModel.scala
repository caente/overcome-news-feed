package com.dimeder.models

import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.dimeder.streams.TwitterStreamer.Tweet
import com.dimeder.exractors.TwitterExtractor.{Sample, Item}
import rx.lang.scala.Observable
import com.dimeder.log
import com.dimeder.models.WordsModel.Delete

/**
 * Created: Miguel A. Iglesias
 * Date: 1/25/14
 */

trait WordsCleaner {
  def getSources: Observable[Long]

  def countWordsBySource(source: Long): Future[Int]

  def removeOldestWords(delete: Delete): Future[Unit]
}

object WordsModel extends WordsCleaner {

  case class Delete(source: Long, document_count: Int)

  RegisterJodaTimeConversionHelpers()

  def upsertSample(sample: Sample) = sample match {
    case Sample(tweet: Tweet, items: Set[Item]) =>
      log.debug(s"persisting $sample")
      val now = DateTime.now
      items.foreach {
        item =>
          val update = $set("text" -> tweet.text) ++ $set("last_update" -> now) ++ $push("history" -> now) ++ $inc("count" -> item.count)
          db("words").update(
            MongoDBObject("source" -> tweet.origin, "word" -> item.item),
            update,
            upsert = true
          )
      }
  }

  def getSources: Observable[Long] = Observable.from(db("sources").find().map(_.getAs[Long]("_id").get).toIterable)

  def countWordsBySource(source: Long): Future[Int] = Future(db("words").count(MongoDBObject("source" -> source)).toInt)

  def removeOldestWords(delete: Delete): Future[Unit] = Future {
    log.debug(s"removing ${delete.document_count} items")
    delete match {
      case Delete(source: Long, extra: Int) =>
        val wordsToRemove = db("words").find(MongoDBObject("source" -> source)).sort(MongoDBObject("last_update" -> -1)).limit(extra).map(_.getAs[ObjectId]("_id").get)
        db("words").remove("_id" $in wordsToRemove.toList)
    }
  }

}
