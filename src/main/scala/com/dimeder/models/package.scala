package com.dimeder

import com.mongodb.casbah.MongoConnection

/**
 * Created: Miguel A. Iglesias
 * Date: 4/18/14
 */
package object models {
  val connection: MongoConnection = MongoConnection("localhost", 27017)

  val db = connection("overcome-news")
}
