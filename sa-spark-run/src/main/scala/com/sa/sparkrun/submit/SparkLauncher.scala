package com.sa.sparkrun.submit

import cats.Parallel
import cats.data.EitherT
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Effect}
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.handlers.{SparkRunner, SpSuccess, SparkRunParam}
import com.sa.sparkrun.params.SparkCommand
import com.sa.sparkrun.service.{JobService, JobServiceImpl}
import io.chrisdavenport.log4cats.StructuredLogger

class SparkLauncher[F[_]]( sparkRunner: SparkRunner[F]
                      , logger: StructuredLogger[F]
                      , blocker: Blocker
                    )(
                      implicit
                      F: Effect[F],
                      p: Parallel[F],
                      cs: ContextShift[F]
                    ) {

  import cats.implicits._

  import io.circe.generic.auto._
  import io.circe.syntax._
//  val testSc = SparkCommand("org.apache.spark.examples.SparkPi","/opt/spark-2.4.0-bin-hadoop2.7/examples/jars/spark-examples_2.11-2.4.0.jar",List("80"))
    val testSc = SparkCommand("org.apache.spark.examples.SparkPi","hdfs:///sparkrun/books/spark-examples_2.11-2.4.0.jar",List("80"))

  // TODO: VERIFY HOW THE BLOCKER WE USED FOR INITIALIZING DOOBIE/HIKARI TRANSACTOR POOL IS USED
  //  WE HAVE ALREADY PROVIDED A THREAD POOL FOR TRANSACT BLOCKER
  //  ONCE CONFIRMED WE NEED NOT USE blocker.blockOn() HERE FOR DB CALLS
  def run(jobS: JobService[F])(implicit F: ConcurrentEffect[F]): EitherT[F,String,Unit] =
    for {
//      _ <- F.delay(println(s"--- SPARK LAUNCHER RUN ---------"))
      queuedJobs <- EitherT.right[String](blocker.blockOn(jobS.allQueued))
      _ <- queuedJobs.map(executeQueuedJob(_, jobS)).parSequence
//      _ <- F.delay(println(s"INSERT TEST JOB JUST FOR TESTING"))
      _ <- EitherT.right[String](jobS.insert(Job(0,java.util.UUID.randomUUID().toString,testSc.asJson.toString)))
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
//    F.pure(false)
  }

  def updateJobStatus(job: Job, spResp: SpSuccess, jobS: JobService[F]): F[Boolean] =
    jobS.setStatusAndAppIdAndResetError(job.id, spResp.appId, ExecutionJobStatus.Submitted, "")

  def updateError(job: Job, error: String, jobS: JobService[F]): F[Boolean] =
    jobS.setError(job.id, error)

}

//    jobS2.allQueued
//      .flatMap(jSeq =>
//        F.delay(println(s"received ${jSeq.length} job instances")) *> F.pure(jSeq)
//      )
//      .flatMap(_.map(process).parSequence)
//      .void

////  //    jobS.allQueued
////  //      .flatMap(jSeq => logger.info(s"received ${jSeq.length} job instances") *> F.pure(jSeq))
////  //      .flatMap(_.map(process).parSequence)
////  //      .void

