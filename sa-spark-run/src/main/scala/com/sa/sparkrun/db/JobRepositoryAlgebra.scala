package com.sa.sparkrun.db

import java.time.Instant
import java.util.UUID

import cats.data.NonEmptyList
import com.sa.sparkrun.db.domain.PagedResult
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.db.models.json.ExecutionResult.ExecutionResult
import doobie.util.update.Update0

trait JobRepositoryAlgebra[F[_]] {

  def insert(j: Job): F[Job]

  def getById(id: Long): F[Option[Job]]

//  def getByUuid(jobUuid: UUID): F[Option[Job]]

//  def getByUuidForRecon(jobUuid: UUID, reconUuid: UUID): F[Option[(JobInstance, Option[Recon])]]

//  def findByReconId(
//                     reconId: Long,
//                     offset: Int,
//                     limit: Int,
//                     fromDate: Option[Instant],
//                     toDate: Option[Instant]
//                   ): F[PagedResult[JobInstance]]

//  def getIncompleteJobsByReconId(id: Long): F[List[JobInstance]]

  def getAllJobs: F[List[Job]]

  def updateRecord(record: Job): F[Unit]

  def findByJobStatus(s: List[ExecutionJobStatus]): F[List[Job]]

//  def getLastSucceededByReconId(reconId: Long): F[Option[Job]]

//  def getLastByReconId(reconId: Long): F[Option[Job]]

  def attachSparkAppId(id: Long, appId: String): F[Unit]

  /**
   * Update a job instance status based on the primary key
   *
   * @param jobId
   * @param status
   * @return
   */
  def updateJobStatus(jobId: Long, status: ExecutionJobStatus): F[Boolean]

  def updateJobStatusAndAppId(jobId: Long, status: ExecutionJobStatus, appId: String): F[Boolean]

  def updateError(jobId: Long, error: String): F[Boolean]

  def updateStatusAndAppIdAndError(jobId: Long, appId: String, status: ExecutionJobStatus, error: String): F[Boolean]

  def updateJobStatusByUUID(jobUUID: String, status: ExecutionJobStatus): F[Boolean]

  def deleteById(id: Long): F[Boolean]

  def deleteByUUID(uuid: String): F[Boolean]
  /**
   * Update a job instance status (in success case) based on the primary key
   *
   * @param jobId
   * @param status
//   * @param resultFiles
   * @param results
   * @return
   */
  def updateJob(
                 jobId: Long,
                 sparkJobId: String,
//                 badRecordsCount: Option[Int],
                 status: ExecutionJobStatus,
//                 resultFiles: List[Dataset],
                 results: ExecutionResult
               ): F[Boolean]


  /**
   * Find all job by list of ids
   * @param ids
   * @return
   */
  def findAllByIds(ids: NonEmptyList[Long]): F[List[Job]]
}
