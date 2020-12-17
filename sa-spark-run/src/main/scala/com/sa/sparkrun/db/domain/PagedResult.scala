package com.sa.sparkrun.db.domain

import io.circe.generic.extras.Configuration
import io.circe.Encoder
import io.circe.generic.extras.semiauto._
import cats.implicits._

/**
  * Generic structure for passing paging/scrolling slices of a query.
  * This requires the ability to Circe Encode T to Json
  * If limit and offset are zero, then total is None and payload is the complete unpaged result
  * This is in slight change mode to avoid PagedResult for when not paging.
  * All UI level is paged now.
  * @param total Total of all records in query being paged. Only set when offset = 0 (else None)
  */
case class PagedResult[T: Encoder](total: Option[Long], offset: Int, limit: Int, payload: List[T]) {
  def transform[A: Encoder](fn: T => A): PagedResult[A] = this.copy(payload = payload.map(fn(_)))
  val isNotPaged                                        = offset == 0 & limit == 0
}

object PagedResult {
  implicit val config = Configuration.default

  implicit def encoder[T: Encoder]: Encoder.AsObject[PagedResult[T]] =
    deriveConfiguredEncoder[PagedResult[T]]

  def from[T: Encoder](pagedRequest: PagedRequest, dbRes: (Option[Long], List[T])): PagedResult[T] =
    PagedResult(dbRes._1, pagedRequest.offset, pagedRequest.limit, dbRes._2)
}
