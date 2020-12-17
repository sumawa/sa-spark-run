package com.sa.sparkrun.db

import java.time._
import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.Async
import cats.implicits._
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.db.models.json.ExecutionResult.ExecutionResult
import com.sa.sparkrun.db.repository.doobie.DoobieMappingTypes._
import com.sa.sparkrun.db.repository.doobie.DoobieContextSharing.dc._
import com.sa.sparkrun.db.repository.doobie.DoobieContextSharing.dc
import doobie._
import doobie.implicits._
import com.sa.sparkrun.db.repository.doobie.MappingDataType._

/**
 *
 * @param xa
 * @tparam F
 */
class DoobieJobRepository[F[_]](xa: Transactor[F])(implicit F: Async[F])
  extends JobRepositoryAlgebra[F] {
  implicit val jobInstanceSchema         = schemaMeta[Job]("jobs")
//  implicit val datesetSchemaMeta         = schemaMeta[Dataset]("dataset")
//  implicit val reconProcessingSchemaMeta = schemaMeta[ReconProcessing]("recon_processing")

//  implicitly[MappedEncoding[LocalDateTime, Instant]]

  val className = getClass.getName

  private val NonCompleteStatus: List[ExecutionJobStatus] =
    List[ExecutionJobStatus](
      ExecutionJobStatus.Running,
      ExecutionJobStatus.Queued
    )

  override def getById(id: Long): F[Option[Job]] = {
    val executeQuery = quote {
      query[Job]
        .filter(_.id == lift(id))
    }

    run(executeQuery)
      .transact(xa)
      .map(_.headOption)
  }

//  override def getByUuid(jobUUID: UUID): F[Option[Job]] = {
//    val executeQuery = quote {
//      query[Job].filter(_.uuid == lift(jobUUID))
//    }
//
//    run(executeQuery).transact(xa).map(_.headOption)
//  }

  /** Ensures the job is associated with given recon. Will be common so in Repo  */
//  override def getByUuidForRecon(
//                                  jobUuid: UUID,
//                                  reconUuid: UUID
//                                ): F[Option[(JobInstance, Option[Recon])]] = {
//    val q = quote {
//      val q1 = query[JobInstance].filter(_.uuid == lift(jobUuid))
//      val q2 = query[Recon].filter(_.uuid == lift(reconUuid))
//      q1.leftJoin(q2).on { case (job, recon) => job.reconId == recon.id }
//    }
//    run(q).transact(xa).map(_.headOption)
//  }
//
//  /**
//   *
//   * @param id
//   * @param offset
//   * @param limit
//   * @param fromDate Instant already munged to appropriate start of day or whatever
//   * @param toDate  Not validated that after fromDate
//   * @return
//   */
//  override def findByReconId(
//                              id: Long,
//                              offset: Index,
//                              limit: Index,
//                              fromDate: Option[Instant],
//                              toDate: Option[Instant]
//                            ): F[PagedResult[JobInstance]] = {
//    val baseQ = dynamicQuery[JobInstance]
//      .filter(record => record.reconId == lift(id))
//      .filterOpt(fromDate)((r, fromDate) => quote(r.createdAt >= fromDate))
//      .filterOpt(toDate)((r, toDate) => quote(r.createdAt <= toDate))
//      .sortBy(record => record.createdAt)(Ord.descNullsLast)
//
//    val totalQ = baseQ.size
//
//    val resultQ = baseQ
//      .drop(offset)
//      .take(limit)
//
//    val transaction = for {
//      total   <- run(totalQ)
//      results <- run(resultQ)
//    } yield (results, total)
//
//    transaction
//      .transact(xa)
//      .map { case (results, total) => PagedResult(total.some, offset, limit, results) }
//  }

//  /**
//   * This function returns the total number of Spark App's that are currently in a non-complete status
//   * for the given recon_id
//   *
//   * @param id - Recon_id for which we run the SQL query via Quill
//   * @return
//   */
//  override def getIncompleteJobsByReconId(id: Long): F[List[JobInstance]] = {
//    val executeQuery = quote {
//      query[JobInstance]
//        .filter(_.reconId == lift(id))
//        .filter(record => liftQuery(NonCompleteStatus).contains(record.jobStatus))
//    }
//
//    run(executeQuery)
//      .transact(xa)
//  }

  /**
   * This function returns the total number of Spark App's that are currently in a non-complete status
   *
   * @return
   */
  override def getAllJobs: F[List[Job]] = {
    val executeQuery = quote {
      query[Job]
//                .filter(j => liftQuery(NonCompleteStatusInt).contains(j.status))
        .filter(record => liftQuery(NonCompleteStatus).contains(record.status))
//        .filter(_.status in NonCompleteStatus)
    }

    run(executeQuery)
      .transact(xa)
  }

  /**
   *
   * @param record
   * @return
   */
  override def updateRecord(record: Job): F[Unit] = {
    val executeQuery = quote {
      query[Job]
        .filter(_.id == lift(record.id))
        .update(lift(record))
    }

    run(executeQuery)
      .transact(xa)
      .void
  }

//  private def qByStatus(s: List[ExecutionJobStatus]) =
//    quote { query[Job].filter(job => liftQuery(s).contains(job.status)) }

  private def qByStatus(s: List[ExecutionJobStatus]) =
    quote { query[Job].filter(j => liftQuery(s).contains(j.status)) }

  override def findByJobStatus(s: List[ExecutionJobStatus]): F[List[Job]] =
    run(qByStatus(s)).transact(xa)

  /**
   * Update a job instance status based on the primary key
   *
   *  @todo Change to return Unit for consistency?
   * @param jobId
   * @param status
   * @return
   */
  override def updateJobStatus(jobId: Long, status: ExecutionJobStatus): F[Boolean] = {
    val updateQ = updateJobStatusQ(jobId, status)

    run(updateQ)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  /**
   * Update a job instance status based on the primary key
   *
   *  @todo Change to return Unit for consistency?
   * @param jobId
   * @param status
   * @return
   */
  override def updateJobStatusAndAppId(jobId: Long, status: ExecutionJobStatus, appId: String): F[Boolean] = {
    val updateQ = updateSparkAppIdAndStatusQ(jobId, appId, status)

    run(updateQ)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  override def updateError(jobId: Long, error: String): F[Boolean] = {
    val updateError = updateErrorQ(jobId, error)

    run(updateError)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  def updateStatusAndAppIdAndError(jobId: Long, appId: String, status: ExecutionJobStatus, error: String): F[Boolean] = {
    val updateAppIdStatusError = updateSparkAppIdAndStatusAndErrorQ(jobId, appId, status, error)

    run(updateAppIdStatusError)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }
  override def updateJobStatusByUUID(jobUUID: String, status: ExecutionJobStatus): F[Boolean] = {
    val updateQ = updateJobStatusByUuidQ(jobUUID, status)

    run(updateQ)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  /**
   * Update a job instance status (in success case) based on the primary key
   */
  override def updateJob(
                 jobId: Long,
                 sparkJobId: String,
                 //                 badRecordsCount: Option[Int],
                 status: ExecutionJobStatus,
                 //                 resultFiles: List[Dataset],
                 results: ExecutionResult
               ): F[Boolean] = {
    val updateQ = quote {
      query[Job]
        .filter(_.id == lift(jobId))
        .update(
          _.status       -> lift(status),
          _.sparkJobId      -> lift(sparkJobId.some),
//          _.badRecordsCount -> lift(badRecordsCount)
        )
    }

    val transaction = for {
      result     <- run(updateQ)
//      _      <- run(insertDatasetsQ)
//      result <- run(updateReconProcessingResultQ)
    } yield result

    transaction
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  override def insert(j: Job): F[Job] =     {
    println(s"Running insert: ")
    val q = quote {
      query[Job]
      .insert(lift(j))
      .returningGenerated(_.id)
    }
    println(s"insert query: ${q}")
    run(q)
      .transact(xa)
      .map(id => j.copy(id = id))
  }

  private def updateJobStatusQ(
                                jobId: Long,
                                status: ExecutionJobStatus
                              ): dc.Quoted[dc.Update[Job]] =
    quote {
      query[Job]
        .filter(_.id == lift(jobId))
        .update(_.status -> lift(status))
    }

  private def updateJobStatusByUuidQ(
                                      job: String,
//                                      job: UUID,
                                      status: ExecutionJobStatus
                                    ): dc.Quoted[dc.Update[Job]] =
    quote {
      query[Job].filter(_.uuid == lift(job)).update(_.status -> lift(status))
    }

  // attach spark app id
  private def attachSparkAppIdQ(id: Long, appId: String) =
    quote {
      query[Job]
        .filter(_.id == lift(id))
        .update(_.sparkJobId -> lift(appId.some))
    }

  override def attachSparkAppId(id: Long, appId: String): F[Unit] =
    run(attachSparkAppIdQ(id, appId)).transact(xa).void

  private def updateSparkAppIdAndStatusQ(id: Long, appId: String, status: ExecutionJobStatus) =
    quote {
      query[Job]
        .filter(_.id == lift(id))
        .update(_.sparkJobId -> lift(appId.some), _.status -> lift(status))
    }

  private def updateSparkAppIdAndStatusAndErrorQ(id: Long, appId: String, status: ExecutionJobStatus, error: String) =
    quote {
      query[Job]
        .filter(_.id == lift(id))
        .update(_.sparkJobId -> lift(appId.some), _.status -> lift(status), _.error -> lift(error.some))
    }

  private def updateErrorQ(id: Long, error: String) =
    quote {
      query[Job]
        .filter(_.id == lift(id))
        .update(_.error -> lift(error.some))
    }


  //  override def listLatestByReconIds(reconIds: NonEmptyList[Long]): F[List[JobInstance]] =
//    (fr"""SELECT j.id, j.uuid, j.recon_id, j.executed_by_user, j.from_date, j.to_date, j.execution_conf, j.spark_job_app_name, j.spark_job_id, j.start_job_time, j.end_job_time, j.job_status, j.created_at, j.updated_at, j.bad_records_count
//         |FROM job_instance j
//         |INNER JOIN
//         |  (SELECT MAX(id) AS max_id,
//         |          recon_id
//         |   FROM job_instance
//         |   GROUP BY recon_id) grouped
//         |   ON j.id = grouped.max_id AND""".stripMargin ++ Fragments.in(fr"j.recon_id", reconIds))
//      .query[JobInstance]
//      .to[List]
//      .transact(xa)

  override def deleteById(id: Long): F[Boolean] = {
    val deleteJob = deleteJobByIdQ(id)

    run(deleteJob)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)

  }

  private def deleteJobByIdQ(id: Long) =
    quote {
      query[Job]
        .filter(_.id == lift(id))
        .delete
    }


  override def deleteByUUID(uuid: String): F[Boolean] = {
    val deleteJob = deleteJobByUUIDQ(uuid)
    run(deleteJob)
      .transact(xa)
      .map(numberRow => if (numberRow > 0) true else false)
  }

  private def deleteJobByUUIDQ(uuid: String) =
    quote{
      query[Job]
        .filter(_.uuid == lift(uuid))
        .delete
    }
  /**
   * Find all job by list of ids
   *
   * @param ids
   * @return
   */
  override def findAllByIds(ids: NonEmptyList[Long]): F[List[Job]] = {
    val q = quote {
      query[Job]
        .filter(job => liftQuery(ids.toList).contains(job.id))
    }
    run(q)
      .transact(xa)
  }
}
