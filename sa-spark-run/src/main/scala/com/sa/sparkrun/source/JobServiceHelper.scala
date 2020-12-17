package com.sa.sparkrun.source

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, IO, Timer}
import com.sa.sparkrun.conf.ConfHelper.{loadCnfF}
import com.sa.sparkrun.conf.{DaemonConf, DatabaseConfig, YarnConf, YarnConfig}
import com.sa.sparkrun.db.{DoobieJobRepository, PooledTransactor}
import com.sa.sparkrun.handlers.standalone.StandaloneRunner
import com.sa.sparkrun.handlers.yarn.YarnRunner
import com.sa.sparkrun.service.{JobService, JobServiceImpl}
//import com.sa.sparkrun.handlers.yarn.MyYarnRunner
import com.sa.sparkrun.params.{StandaloneConf, YarnParam}
import com.sa.sparkrun.submit.yarn.YarnClientHelper
import fs2.Stream

object JobServiceHelper{

  import pureconfig.generic.auto._

  def loadSource[F[_]](sourceType: String
                       , externalConfigPath: java.nio.file.Path, blocker: Blocker)
                      (implicit F: ConcurrentEffect[F]
                       , timer: Timer[F]
                       , cs: ContextShift[F]) = sourceType match {
    case "sql" =>
      for {
        databaseConf <- loadCnfF[F,DatabaseConfig](externalConfigPath, DatabaseConfig.namespace, blocker)
        _ <- Stream.eval(F.delay(println(s"got databaseConf: ${databaseConf}")))
        xa <- Stream.eval(PooledTransactor[F](databaseConf))
        _ <- Stream.eval(F.delay(println(s"GOT ### XA: ${xa}")))
        jobR           = new DoobieJobRepository[F](xa)
        _ <- Stream.eval(F.delay(println(s"GOT ### DoobieJobRepository: ${jobR}")))
        jobS = new JobServiceImpl[F](jobR)
      } yield (jobS)
    case _ =>
      for {
        databaseConf <- loadCnfF[F,DatabaseConfig](externalConfigPath, DatabaseConfig.namespace, blocker)
        _ <- Stream.eval(F.delay(println(s"got databaseConf: ${databaseConf}")))
        xa <- Stream.eval(PooledTransactor[F](databaseConf))
        _ <- Stream.eval(F.delay(println(s"GOT ### XA: ${xa}")))
        jobR           = new DoobieJobRepository[F](xa)
        _ <- Stream.eval(F.delay(println(s"GOT ### DoobieJobRepository: ${jobR}")))
        jobS = new JobServiceImpl[F](jobR)
      } yield (jobS)

  }
}
