package com.sa.sparkrun.handlers

import cats.data.EitherT
import cats.effect.ConcurrentEffect
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.submit.AppStatus

import cats.effect.{ConcurrentEffect, ContextShift, Timer, Blocker}
import com.sa.sparkrun.conf.ConfHelper.{loadCnfF}
import com.sa.sparkrun.conf.{DaemonConf, YarnConf}
import com.sa.sparkrun.handlers.standalone.{StandaloneRunner}
import com.sa.sparkrun.handlers.yarn.YarnRunner
//import com.sa.sparkrun.handlers.yarn.MyYarnRunner
import com.sa.sparkrun.params.{StandaloneConf, YarnParam}
import com.sa.sparkrun.submit.yarn.YarnClientHelper
import fs2.Stream
import pureconfig.generic.auto._


sealed trait SparkResponse {
  sr =>
  def fold[A](onError: Throwable => A, onSuccess: (String, AppStatus) => A): A = {
    println(s"fold !!!!!! ${onSuccess}")
    sr match {
      case SpSuccess(appId: String, status: AppStatus) => onSuccess(appId, status)
      case SpFailure(err)                             => onError(err)
    }
  }
}

case class SpSuccess(appId: String, status: AppStatus)   extends SparkResponse
case class SpFailure(err: Throwable)                  extends SparkResponse

sealed trait SparkClient
final case object Standalone extends SparkClient
final case object Yarn extends SparkClient


trait SparkRunner[F[_]]{
  def submit(job: Job)(implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess]
}

object SparkRunner{

  def loadRunner[F[_]: ConcurrentEffect: ContextShift: Timer](sparkType: String
                                                              , externalConfigPath: java.nio.file.Path, blocker: Blocker)
  = sparkType match {
    case "yarn" =>
      for{
        yarnConf <- loadCnfF[F,YarnConf](externalConfigPath, YarnConf.namespace, blocker)
        daemonConf <- loadCnfF[F,DaemonConf](externalConfigPath, DaemonConf.namespace, blocker)
        yarnParam = YarnParam(yarnConf, daemonConf)
        yarnClient <- Stream.eval(YarnClientHelper.build[F](yarnConf.clientConfig, blocker))
        myYarnParam = MyYarnRunParam(yarnClient,yarnParam)
        runner = new YarnRunner[F](myYarnParam)
      } yield runner
    case _ =>
      for {
        standaloneConf <- loadCnfF[F,StandaloneConf](externalConfigPath, StandaloneConf.namespace, blocker)
        myStandaloneParam = StandaloneRunParam(standaloneConf)
        runner = new StandaloneRunner[F](myStandaloneParam)
      } yield (runner)
  }
}


