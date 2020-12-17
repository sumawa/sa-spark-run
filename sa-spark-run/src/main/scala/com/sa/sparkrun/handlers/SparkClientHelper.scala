package com.sa.sparkrun.handlers

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import com.sa.sparkrun.conf.ConfHelper.loadCnfF
import com.sa.sparkrun.conf.{DaemonConf, SubmitterConf, YarnConfig}
import com.sa.sparkrun.params.{StandaloneConf, YarnParam}
import com.sa.sparkrun.submit.yarn.YarnClientHelper
import fs2.Stream
import io.circe.Json
import org.apache.hadoop.yarn.client.api.YarnClient
import org.http4s.{Header, Method, Request, Uri}
import pureconfig.generic.auto._

sealed trait SparkRunParam
//final case class StandaloneRunParam(standAloneParam: StandAloneParam) extends SparkRunParam
final case class StandaloneRunParam(standaloneConf: StandaloneConf) extends SparkRunParam
final case class MyYarnRunParam(yarnClient: YarnClient, yarnParam: YarnParam) extends SparkRunParam

object SparkClientHelper{

  def loadClient[F[_]](sparkType: String
                       , externalConfigPath: java.nio.file.Path
                      , blocker: Blocker)
                      (implicit F: ConcurrentEffect[F]
                       , timer: Timer[F]
                       , cs: ContextShift[F])= sparkType match {
    case "yarn" =>
      val c = for{
        yarnConf <- loadCnfF[F,YarnConfig](externalConfigPath,YarnConfig.namespace, blocker)
        submitterConf <- loadCnfF[F,SubmitterConf](externalConfigPath, SubmitterConf.namespace, blocker)
        daemonConf <- loadCnfF[F,DaemonConf](externalConfigPath, DaemonConf.namespace, blocker)
        yarnParam = YarnParam(yarnConf, submitterConf, daemonConf)
        yarnClient <- Stream.eval(YarnClientHelper.build[F](yarnConf.clientConfig, blocker))
      } yield (MyYarnRunParam(yarnClient,yarnParam))
      c
    case _ =>

      for {
        standaloneConf <- loadCnfF[F,StandaloneConf](externalConfigPath, StandaloneConf.namespace, blocker)
        myStandaloneParam = StandaloneRunParam(standaloneConf)

      } yield (myStandaloneParam)
  }

  def getType[T]()(implicit man: Manifest[T]) = {
    val tType = man.runtimeClass.getSimpleName.toLowerCase
    println(s"tType: ${tType}")
    man.runtimeClass.getSimpleName match {
      case "Yarn" => "Its Yarn"
      case "Standalone" =>
    }
  }
  //    YarnClientHelper.build[IO](sparkParam.yarnConfig.clientConfig)
}

