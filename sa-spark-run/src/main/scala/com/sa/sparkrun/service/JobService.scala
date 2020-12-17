package com.sa.sparkrun.service

import cats.effect.concurrent.Ref
import cats.effect.{Effect, Sync}
import com.sa.sparkrun.db.JobRepositoryAlgebra
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.db.models.ExecutionJobStatus

case class RunningJob(
                       id: Long,
                       appId: String,
                       ctxId: Option[Int],
                       status: ExecutionJobStatus,
                       jobInstance: Job
                     )

trait JobService[F[_]] {
  def insert(job: Job): F[Job]
  def allQueued: F[List[Job]]
  def all: F[List[Job]]
  def allNonTerminated: F[List[RunningJob]]
  def byId(id: Long): F[Option[Job]]
  def setSubmitted(jobId: Long, appId: String): F[Unit]
  def setRunning(jobId: Long, appId: String): F[Unit]
  def setSucceeded(jobId: Long): F[Unit]
  def setKilled(jobId: Long): F[Unit]
  def setFailed(jobId: Long): F[Unit]

  def setStatusOnly(jobId: Long, status: ExecutionJobStatus): F[Boolean]
  def setAppIdOnly(jobId: Long, appId: String): F[Unit]
  def setStatusAndAppId(jobId: Long, appId: String, status: ExecutionJobStatus): F[Boolean]
  def setError(jobId: Long, error: String): F[Boolean]
  def setStatusAndAppIdAndResetError(jobId: Long, appId: String, status: ExecutionJobStatus, error: String): F[Boolean]

  def deleteById(id: Long): F[Boolean]
  def deleteByUUID(uuid: String): F[Boolean]
}

class DummyJobInstanceService[F[_]](st: Ref[F, Seq[Job]])(
  implicit
  F: Sync[F]
) extends JobService[F] {
  import cats.implicits._

  override def insert(job: Job): F[Job] = ???

  override def allQueued: F[List[Job]] =
    st.get.map(_.filter(_.status == ExecutionJobStatus.Queued).toList)

  override def all: F[List[Job]] =
    st.get.map(_.toList)

  override def allNonTerminated: F[List[RunningJob]] =
    st.get.map(
      _.filter(
        j =>
          j.status == ExecutionJobStatus.Running || j.status == ExecutionJobStatus.Submitted
      ).toList
        .map(j => RunningJob(j.id, j.sparkJobId.get, j.ctxId, j.status, j))
    )

  override def byId(id: Long): F[Option[Job]] =
    st.get.map(_.find(_.id == id))

  override def setSubmitted(jobId: Long, appId: String): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Submitted, Some(appId))

  override def setRunning(jobId: Long, appId: String): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Running, Some(appId))

  override def setSucceeded(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Succeeded, None)

  override def setKilled(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Killed, None)

  override def setFailed(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Failed, None)

  def setStatusOnly(jobId: Long, status: ExecutionJobStatus): F[Boolean] = ???

  def setAppIdOnly(jobId: Long, appId: String): F[Unit] = ???

  def setStatusAndAppId(jobId: Long, appId: String, status: ExecutionJobStatus): F[Boolean] = ???

  def setError(jobId: Long, error: String): F[Boolean] = ???


  override def setStatusAndAppIdAndResetError(jobId: Long, appId: String, status: ExecutionJobStatus, error: String): F[Boolean] = ???


  override def deleteById(id: Long): F[Boolean] = ???


  override def deleteByUUID(uuid: String): F[Boolean] = ???

  private def setStatus(jobId: Long, sts: ExecutionJobStatus, appId: Option[String]): F[Unit] =
    for {
      all    <- st.get
      oneOpt = all.find(_.id == jobId)
      one    = oneOpt.getOrElse(throw new RuntimeException(s"not found #$jobId"))
      cpd    = one.copy(status = sts)
      cpdW   = cpd.copy(sparkJobId = appId)
      _      <- st.update(all => all.filterNot(_ == one) :+ cpdW)
    } yield ()
}

class JobServiceImpl[F[_]](rep: JobRepositoryAlgebra[F])(
  implicit
  F: Effect[F]
) extends JobService[F] {
  import cats.implicits._

  override def insert(job: Job): F[Job] = rep.insert(job)

  def allQueued: F[List[Job]] =
    rep.findByJobStatus(ExecutionJobStatus.Queued :: Nil)

  override def all: F[List[Job]] =
    rep.getAllJobs

  def allNonTerminated: F[List[RunningJob]] =
    rep
      .findByJobStatus(ExecutionJobStatus.Submitted :: ExecutionJobStatus.Running :: Nil)
      .map { js =>
        js.filter(_.sparkJobId.isDefined)
          .map(j => RunningJob(j.id, j.sparkJobId.get, j.ctxId, j.status, j))
      }

  def byId(id: Long): F[Option[Job]] =
    rep.getById(id)

  // set statuses
  def setSubmitted(jobId: Long, appId: String): F[Unit] =
    for {
      _ <- setStatus(jobId, ExecutionJobStatus.Submitted)
      _ <- rep.attachSparkAppId(jobId, appId)
    } yield ()

  def setRunning(jobId: Long, appId: String): F[Unit] =
    for {
      _ <- setStatus(jobId, ExecutionJobStatus.Running)
      _ <- rep.attachSparkAppId(jobId, appId)
    } yield ()

  def setSucceeded(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Succeeded)

  def setKilled(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Killed)

  def setFailed(jobId: Long): F[Unit] =
    setStatus(jobId, ExecutionJobStatus.Failed)

  private def setStatus(jobId: Long, s: ExecutionJobStatus): F[Unit] =
    rep.updateJobStatus(jobId, s).void

  private def setStatusBoolean(jobId: Long, s: ExecutionJobStatus): F[Boolean] =
    rep.updateJobStatus(jobId, s)

  private def setStatusAndAppId(jobId: Long, s: ExecutionJobStatus, appId: String): F[Boolean] =
    rep.updateJobStatusAndAppId(jobId, s, appId)

  def setError(jobId: Long, error: String): F[Boolean] =
    rep.updateError(jobId, error)


  def setStatusAndAppIdAndResetError(jobId: Long, appId: String, status: ExecutionJobStatus, error: String): F[Boolean] =
    rep.updateStatusAndAppIdAndError(jobId, appId, status, error)


  override def deleteById(id: Long): F[Boolean] =
    rep.deleteById(id)


  override def deleteByUUID(uuid: String): F[Boolean] =
    rep.deleteByUUID(uuid)

  def setStatusOnly(jobId: Long, status: ExecutionJobStatus): F[Boolean] =
    for {
      b <- setStatusBoolean(jobId, status)
    } yield b

  def setAppIdOnly(jobId: Long, appId: String): F[Unit] =
    for {
      _ <- rep.attachSparkAppId(jobId, appId)
    } yield ()


  def setStatusAndAppId(jobId: Long, appId: String, status: ExecutionJobStatus): F[Boolean] =
    for {
      b <- setStatusAndAppId(jobId, status, appId)
    } yield b

//  override def setError(jobId: Long, error: String): F[Boolean] =
//    for {
//      b <- setError(jobId,error)
//    } yield b
}
