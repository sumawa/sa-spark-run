package com.sa.sparkrun.handlers

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

object SparkRunnerHelper{

  def loadRunner[F[_]](sparkType: String
                       , externalConfigPath: java.nio.file.Path, blocker: Blocker)
                      (implicit F: ConcurrentEffect[F]
                       , timer: Timer[F]
                       , cs: ContextShift[F])= sparkType match {
    case "yarn" =>
      val c = for{
        yarnConf <- loadCnfF[F,YarnConf](externalConfigPath, YarnConf.namespace, blocker)
        daemonConf <- loadCnfF[F,DaemonConf](externalConfigPath, DaemonConf.namespace, blocker)
        yarnParam = YarnParam(yarnConf, daemonConf)
        yarnClient <- Stream.eval(YarnClientHelper.build[F](yarnConf.clientConfig, blocker))
        myYarnParam = MyYarnRunParam(yarnClient,yarnParam)
        runner = new YarnRunner[F](myYarnParam)
      } yield runner
      c
    case _ =>
      for {
        standaloneConf <- loadCnfF[F,StandaloneConf](externalConfigPath, StandaloneConf.namespace, blocker)
        myStandaloneParam = StandaloneRunParam(standaloneConf)
        runner = new StandaloneRunner[F](myStandaloneParam)
      } yield (runner)
  }
}
