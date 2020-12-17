package com.sa.sparkrun.db.repository.doobie

import java.time.{Instant, LocalDate}

import doobie.quill.DoobieContext.MySQL
import io.getquill.SnakeCase
import io.getquill.context.sql.SqlContext

object DoobieContextSharing {

  trait Quotes {
    this: SqlContext[_, _] =>

    implicit class TimestampQuotes(left: Instant) {
      def >(right: Instant)  = quote(infix"$left > $right".as[Boolean])
      def <(right: Instant)  = quote(infix"$left < $right".as[Boolean])
      def >=(right: Instant) = quote(infix"$left >= $right".as[Boolean])
      def <=(right: Instant) = quote(infix"$left <= $right".as[Boolean])
    }

    implicit class LocalDateQuotes(left: LocalDate) {
      def >(right: LocalDate)  = quote(infix"$left > $right".as[Boolean])
      def <(right: LocalDate)  = quote(infix"$left < $right".as[Boolean])
      def >=(right: LocalDate) = quote(infix"$left >= $right".as[Boolean])
      def <=(right: LocalDate) = quote(infix"$left <= $right".as[Boolean])
    }
  }

  lazy val dc = new MySQL(SnakeCase) with Quotes
}
