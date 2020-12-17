package com.sa.sparkrun.db.domain

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._

/*** Standard parameters for a request that wants a PagedResult, ie. scrolling through the result set.  */
case class PagedRequest(offset: Int, limit: Int)

object PagedRequest {
  implicit val conf: Configuration                 = Configuration.default
  implicit val codec: Codec.AsObject[PagedRequest] = deriveConfiguredCodec[PagedRequest]
}
