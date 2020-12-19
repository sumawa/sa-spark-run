package com.sa.sparkrun

import cats.ApplicativeError
import cats.effect.ConcurrentEffect
import io.chrisdavenport.log4cats.StructuredLogger
import cats.implicits._

object SparkRunImplicits {

  def logE[F[_]](ioa: F[_])(logger: StructuredLogger[F])
                (implicit F: ConcurrentEffect[F]): F[Unit] =
    ioa.attempt.flatMap(et => et.fold(ex => logger.error(ex)(ex.getMessage), _ => F.pure(())))

  def trigger[F[_]](ioa: F[_])
    (implicit F: ConcurrentEffect[F]): F[Unit] =
    ioa.attempt.flatMap(et => et.fold(ex => F.unit, _ => F.unit))

  import cats.data.EitherT

  def triggerEither[F[_],A](ioa: EitherT[F,String,A])
                   (implicit F: ConcurrentEffect[F]): F[Unit] =
    ioa.fold(ex => F.unit, _ => F.unit)

}
