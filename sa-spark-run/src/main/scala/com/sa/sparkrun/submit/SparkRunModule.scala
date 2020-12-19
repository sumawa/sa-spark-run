package com.sa.sparkrun.submit

import cats.Parallel
import cats.data.{EitherT, Kleisli}
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Effect, Timer}
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.handlers.{SparkRunner, SpSuccess}
import com.sa.sparkrun.params.SparkCommand
import com.sa.sparkrun.service.JobService
import io.chrisdavenport.log4cats.StructuredLogger

class SparkRunModule[F[_]: Effect: Parallel: ContextShift](
                            sparkRunner: SparkRunner[F]
                            , jobService: JobService[F]
                            , blocker: Blocker
                    ) {

  import cats.implicits._
  import io.circe.generic.auto._
  import io.circe.syntax._
  val testSc = SparkCommand("org.apache.spark.examples.SparkPi","/opt/spark-2.4.0-bin-hadoop2.7/examples/jars/spark-examples_2.11-2.4.0.jar",List("80"))
//    val testSc = SparkCommand("org.apache.spark.examples.SparkPi","hdfs:///sparkrun/books/spark-examples_2.11-2.4.0.jar",List("80"))

  // TODO: VERIFY HOW THE BLOCKER WE USED FOR INITIALIZING DOOBIE/HIKARI TRANSACTOR POOL IS USED
  //  WE HAVE ALREADY PROVIDED A THREAD POOL FOR TRANSACT BLOCKER
  //  ONCE CONFIRMED WE NEED NOT USE blocker.blockOn() HERE FOR DB CALLS
  def run()(implicit F: ConcurrentEffect[F]): EitherT[F,String,Unit] =
    for {
      queuedJobs <- EitherT.right[String](blocker.blockOn(jobService.allQueued))
      res <- queuedJobs.map(executeQueuedJob(_, jobService)).parSequence
      _ <- EitherT.right[String](jobService
        .insert(Job(0,java.util.UUID.randomUUID().toString,testSc.asJson.toString)))
    } yield ()

  def executeQueuedJob(job: Job, jobS: JobService[F])(implicit F: ConcurrentEffect[F]) = for{
    spResp <- EitherT.right[String](handleErrorsIfAny(job,jobS))
  } yield ()

  def handleErrorsIfAny(job: Job, jobS: JobService[F])(implicit F: ConcurrentEffect[F]) =
    {
      val sparkResponse = blocker.blockOn(sparkRunner.submit(job))
      sparkResponse.fold( e => handleError(e, job, jobS), (sp) => updateJobStatus(job, sp, jobS))
    }.flatten

  def handleError(str: String, job: Job, jobS: JobService[F])(implicit F: ConcurrentEffect[F]) = {
    println(s"=== something bad === ${str}")
    updateError(job, str, jobS)
  }

  def updateJobStatus(job: Job, spResp: SpSuccess, jobS: JobService[F]): F[Boolean] =
    jobS.setStatusAndAppIdAndResetError(job.id, spResp.appId, ExecutionJobStatus.Submitted, "")

  def updateError(job: Job, error: String, jobS: JobService[F]): F[Boolean] =
    jobS.setError(job.id, error)

}

//import com.sa.sparkrun.service.JobService
//import com.sa.sparkrun.handlers.SparkRunner

//object SparkRunModule{
//
//  def moduleFromPath[F[_]](externalConfigPath: java.nio.file.Path, blocker: Blocker)(
//    implicit
//    F: ConcurrentEffect[F],
//    timer: Timer[F],
//    cs: ContextShift[F],
//    par: Parallel[F]
//  )
//  : Kleisli[F,java.nio.file.Path, SparkRunModule] =
//    for {
//      sparkRunner <- SparkRunner.fromExtConfigPath[F](externalConfigPath)
//      jobService <- JobService.fromExtConfigPath[F](externalConfigPath)
//    } yield (new SparkRunModule[F](sparkRunner,jobService,blocker))
//}
