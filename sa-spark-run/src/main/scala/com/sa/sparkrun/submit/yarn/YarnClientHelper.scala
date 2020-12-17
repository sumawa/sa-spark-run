package com.sa.sparkrun.submit.yarn

sealed trait SparkClient
final case object Standalone extends SparkClient
final case object Yarn extends SparkClient

import cats.effect.{Blocker, Concurrent, ConcurrentEffect, ContextShift, Sync, Timer}

import org.apache.hadoop.yarn.client.api.YarnClient
import org.apache.hadoop.yarn.conf.YarnConfiguration

object YarnClientHelper {
  //  private def limitExc[F[_]](
  //                              implicit
  //                              F: ConcurrentEffect[F],
  //                              timer: Timer[F],
  //                              cs: ContextShift[F]
  //                            ): F[YarnClient] =
  //    F.raiseError(new RuntimeException(s"connection limit of  ${timeout.toString} exceeded"))

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
  private def doMakeYC[F[_]](clientConfig: Map[String, String])(
    implicit
    F: ConcurrentEffect[F],
    timer: Timer[F],
    cs: ContextShift[F]
  ): F[YarnClient] =
    for {
      _ <- F.delay(println("--------- CREATING YARN CLIENT --------"))
      client <- F.delay(YarnClient.createYarnClient)
//      val confMap = Map(
//
////        "fs.defaultFS" -> "hdfs://127.0.0.1/opt/books"
//        "fs.defaultFS" -> "hdfs://127.0.0.1:50070"
//        , "yarn.resourcemanager.hostname" -> "127.0.0.1"
//        , "yarn.resourcemanager.webapp.address" -> "127.0.0.1:10800"
//
//      )
//      config = createYarnConfiguration(confMap)
            config = createYarnConfiguration(clientConfig)
      _ <- F.delay(println(s"INITIALIZING YARN CLIENT WITH ${config.toString}"))
      _ <- F.delay(client.init(config))
      _ <- F.delay(println("--------- INIT YARN CLIENT SUCCESSFULLY --------"))
      _ <- F.delay(println("--------- STARTING YARN CLIENT --------"))
      _ <- F.delay {
        scala.util.Try(client.start()) match {
          case scala.util.Success(x) => println(s"---- SUCCESSFULY STARTED !!!!! ${x} ----")
          case scala.util.Failure(ex) =>
            println(s"---- FAIL TO START YARN CLIENT !!!!! ${ex.getMessage} ----")
            ex.printStackTrace()
        }
      }
      _ <- F.delay({
        /*
          This requires handling runtime exception
          Sometimes other than the reason, that RM is down, it could also be protocol
          related issues

            https://stackoverflow.com/questions/25771286/yarn-application-master-unable-to-connect-to-resource-manager

          IPv6 vs IPv4
          Hence good strategy would be to set a timeout before initializing client
         */
        try(client.createApplication()) catch {
          case ex: Exception =>
            println(s"---- YARN APPLICATION CREATION EXCEPTION !!!!! ${ex.getMessage} ----")
        }
      })
    } yield client

  // Use Race/Timer related waiting to handle RM connection issue.
  def build[F[_]](clientConfig: Map[String, String], blocker: Blocker)(
    implicit
    F: ConcurrentEffect[F],
    timer: Timer[F],
    cs: ContextShift[F]
  ): F[YarnClient] =
//    doMakeYC[F](clientConfig)
    makeAndConnect[F](blocker,clientConfig)

  import scala.concurrent.duration._
  def makeAndConnect[F[_]](blocker: Blocker, clientConfig: Map[String,String])(
                            implicit
                            F: ConcurrentEffect[F],
                            timer: Timer[F],
                            cs: ContextShift[F]
                          ): F[YarnClient] =
    F.race(timer.sleep(5 seconds)
      , blocker.blockOn(doMakeYC(clientConfig)))
      .flatMap(_.fold(_ => limitExc, cl => F.pure(cl)))

    private def limitExc[F[_]](
                                implicit
                                F: ConcurrentEffect[F],
                                timer: Timer[F],
                                cs: ContextShift[F]
                              ): F[YarnClient] =
      F.raiseError(new RuntimeException(s"connection limit of  5 seconds exceeded"))
//      F.raiseError(new RuntimeException(s"connection limit of  ${timeout.toString} exceeded"))

}