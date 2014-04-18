package com.dimeder.streams

import twitter4j._
import rx.lang.scala.{Observable, Subject}


/**
 * Created: Miguel A. Iglesias
 * Date: 1/23/14
 */

object TwitterStreamer {


  case class Tweet(origin: Long, text: String)

  case class TwitterAccount(account_id: Long, twitterCredentials: TwitterCredentials)

  case class TwitterCredentials(
                                 consumer_key: String,
                                 consumer_secret: String,
                                 access_token: String,
                                 token_secret: String
                                 )


  def apply(credentials: TwitterCredentials): Observable[Tweet] = {
    val tweetsStream = Subject[Tweet]()
    val twitterConfig = createTwitterConfig(credentials)
    val twitterStream = new TwitterStreamFactory(twitterConfig).getInstance
    twitterStream.addListener(userStreamListener(tweetsStream))
    twitterStream.user()
    tweetsStream
  }

  def createTwitterConfig(credentials: TwitterCredentials) = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(credentials.consumer_key)
    .setOAuthConsumerSecret(credentials.consumer_secret)
    .setOAuthAccessToken(credentials.access_token)
    .setOAuthAccessTokenSecret(credentials.token_secret)
    .build

  def userStreamListener(tweetsStream:Subject[Tweet]) = new UserStreamListener {
    def onFriendList(friendIds: Array[Long]): Unit = {}

    def onUserListUnsubscription(subscriber: User, listOwner: User, list: UserList): Unit = {}

    def onStallWarning(warning: StallWarning): Unit = {}

    def onBlock(source: User, blockedUser: User): Unit = {}

    def onUserListSubscription(subscriber: User, listOwner: User, list: UserList): Unit = {}

    def onFollow(source: User, followedUser: User): Unit = {}

    def onUserListMemberAddition(addedMember: User, listOwner: User, list: UserList): Unit = {}

    def onDirectMessage(directMessage: DirectMessage): Unit = {}

    def onUserListUpdate(listOwner: User, list: UserList): Unit = {}

    def onUnblock(source: User, unblockedUser: User): Unit = {}

    def onUserProfileUpdate(updatedUser: User): Unit = {}

    def onException(ex: Exception): Unit = {}

    def onUserListMemberDeletion(deletedMember: User, listOwner: User, list: UserList): Unit = {}

    def onDeletionNotice(directMessageId: Long, userId: Long): Unit = {}

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

    def onFavorite(source: User, target: User, favoritedStatus: Status): Unit = {}

    def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

    def onUnfavorite(source: User, target: User, unfavoritedStatus: Status): Unit = {}

    def onStatus(status: Status): Unit = {
      tweetsStream.onNext(Tweet(status.getUser.getId, status.getText))
    }

    def onUserListDeletion(listOwner: User, list: UserList): Unit = {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}

    def onUserListCreation(listOwner: User, list: UserList): Unit = {}
  }


}
