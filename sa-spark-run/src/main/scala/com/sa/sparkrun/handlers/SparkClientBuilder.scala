//package com.sa.sparkrun.handlers
//
//import cats.effect.{ConcurrentEffect, ContextShift, Timer}
//import com.sa.sparkrun.conf.ConfHelper.loadCnfF
//import com.sa.sparkrun.conf.{DaemonConf, SubmitterConf, YarnConfig}
//import com.sa.sparkrun.params.YarnParam
//import com.sa.sparkrun.submit.yarn.YarnClientHelper
//import fs2.Stream
//
//trait SparkClientBuilder[A] {
//  def build[F[_],A](externalConfigPath: java.nio.file.Path)
//                   (implicit F: ConcurrentEffect[F]
//                    , timer: Timer[F]
//                    , cs: ContextShift[F]): Stream[F,A]
//}
//
//object SparkClientInstances{
//  implicit val standaloneClient: SparkClientBuilder[StandaloneRunParam] =
//    new SparkClientBuilder[MyYarnRunParam] {
//
//      //      override def build[StandaloneRunParam](): StandaloneRunParam = ???
//      override def build[F[_],MyYarnRunParam](externalConfigPath: java.nio.file.Path)
//                                             (implicit F: ConcurrentEffect[F]
//                                              , timer: Timer[F]
//                                              , cs: ContextShift[F]): Stream[F,MyYarnRunParam] = for{
//        yarnConf <- loadCnfF[F,YarnConfig](externalConfigPath,YarnConfig.namespace)
//        submitterConf <- loadCnfF[F,SubmitterConf](externalConfigPath, SubmitterConf.namespace)
//        daemonConf <- loadCnfF[F,DaemonConf](externalConfigPath, DaemonConf.namespace)
//        yarnParam = YarnParam(yarnConf, submitterConf, daemonConf)
//        yarnClient <- getYC[F](yarnConf.clientConfig)
//        //        yarnClient = YarnClientHelper.build[F](yarnConf.clientConfig)
//      } yield (MyYarnRunParam(yarnClient,yarnParam))
//
//      def getYC[F[_]](clientConfig: Map[String,String])(implicit F: ConcurrentEffect[F]
//                                                        , timer: Timer[F]
//                                                        , cs: ContextShift[F]) =
//        Stream.eval(YarnClientHelper.build[F](clientConfig))
//
//    }
//}
