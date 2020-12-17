package com.sa.sparkrun.submit.yarn

import cats.effect.{Blocker, Concurrent, ConcurrentEffect, ContextShift, Sync, Timer}
import io.chrisdavenport.log4cats.StructuredLogger
import org.apache.hadoop.yarn.client.api.YarnClient
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.concurrent.duration.FiniteDuration

class ClientFactory[F[_]](
                           logger: StructuredLogger[F],
                           clientConfig: Map[String, String],
                           timeout: FiniteDuration,
                           blocker: Blocker
                         )(
                           implicit
                           F: ConcurrentEffect[F],
                           timer: Timer[F],
                           cs: ContextShift[F]
                         ) {

  private def limitExc: F[YarnClient] =
    F.raiseError(new RuntimeException(s"connection limit of  ${timeout.toString} exceeded"))

  private def createYarnConfiguration(clientConfig: Map[String, String]): YarnConfiguration = {
    val yarnConfiguration = new YarnConfiguration()
    clientConfig.foreach {
      case (key, v) => yarnConfiguration.set(key, v)
    }
    yarnConfiguration
  }

  import cats.syntax.flatMap._
  import cats.syntax.functor._

  // a bit messy now, but it's for a sacred reason
  private def doMake: F[YarnClient] =
    for {
      _      <- F.delay(println("--------- CREATING YARN CLIENT --------"))
      client <- F.delay(YarnClient.createYarnClient)
      config = createYarnConfiguration(clientConfig)
      _      <- F.delay(println(s"INITIALIZING YARN CLIENT WITH ${config.toString}"))
      _      <- F.delay(client.init(config))
      _      <- logger.info("starting yarn-client")
      _      <- F.delay(client.start())
      _      <- logger.info("successfully started yarn-client")
    } yield client

  def makeAndConnect: F[YarnClient] =
    F.race(timer.sleep(timeout), blocker.blockOn(doMake))
      .flatMap(_.fold(_ => limitExc, cl => F.pure(cl)))
}
