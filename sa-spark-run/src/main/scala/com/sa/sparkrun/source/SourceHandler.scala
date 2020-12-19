package com.sa.sparkrun.source

import cats.data.EitherT
import cats.effect.ConcurrentEffect
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.submit.AppStatus
//import com.sa.sparkrun.submit.{AppStatus, SpSuccess}

//sealed trait SparkResponse {
//  sr =>
//  def fold[A](onError: Throwable => A, onSuccess: (String, AppStatus) => A): A = {
//    println(s"fold !!!!!! ${onSuccess}")
//    sr match {
//      case SpSuccess(appId: String, status: AppStatus) => onSuccess(appId, status)
//      case SpFailure(err)                             => onError(err)
//    }
//  }
//}
//
//case class SpSuccess(appId: String, status: AppStatus)   extends SparkResponse
//case class SpFailure(err: Throwable)                  extends SparkResponse
//
//sealed trait SparkClient
//final case object Standalone extends SparkClient
//final case object Yarn extends SparkClient
//
//
//trait SparkRunner[F[_]]{
//  def submit(job: Job)(implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess]
//}

