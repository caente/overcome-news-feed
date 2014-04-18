package com.dimeder.models

import com.mongodb.casbah.Imports._
import com.dimeder.streams.TwitterStreamer.TwitterCredentials

/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */
object TwitterModel {

  case class TwitterAccount(account_id: Long, twitterCredentials: TwitterCredentials)

  def getTwitterAccount(_id: Long): Option[TwitterCredentials] =
    db("twitter_accounts").findOne(MongoDBObject("_id" -> _id)).map {
      dbo =>
        TwitterCredentials(
          dbo.get("consumer_key").toString,
          dbo.get("consumer_secret").toString,
          dbo.get("access_token").toString,
          dbo.get("token_secret").toString
        )
    }
}
