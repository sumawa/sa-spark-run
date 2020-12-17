package com.sa.sparkrun.db.domain.job

import java.time.{Instant, LocalDate}
import java.util.UUID

import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.db.models.ExecutionJobStatus.Queued

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}

//final case class Job(
//                      id: Long = 0L,
//                      uuid: UUID = UUID.randomUUID(),
////                      reconId: Long,
//                      executedByUser: Long,
//                      fromDate: LocalDate,
//                      toDate: LocalDate,
//                      //                              executionConf: ExecutionConf,
//                      sparkJobAppName: String, // generic app name
//                      sparkJobId: Option[String] = None,
//                      startJobTime: Option[Instant] = None,
//                      endJobTime: Option[Instant] = None,
//                      jobStatus: ExecutionJobStatus = Queued,
//                      createdAt: Instant = Instant.now(),
//                      updatedAt: Instant = Instant.now(),
//                      badRecordsCount: Option[Int] = None
//                            )

//case class Job(id: Int, uuid: String, command: String, sparkJobId: Option[String] = None, sparkJobAppName: Option[String] = None
//               , startJobTime: Option[String] = None, endJobTime: Option[String] = None, status: Option[Int] = None
//               , createdAt: Option[String] = None, updatedAt: Option[String] = None
//               , executedByUser: Option[Int] = None, ctxId: Option[Int] = None)

case class Job(id: Int = 0
               , uuid: String = UUID.randomUUID().toString
               , command: String
//               , status: ExecutionJobStatus = Queued
               , status: ExecutionJobStatus = Queued
               , sparkJobId: Option[String] = None
               , sparkJobAppName: Option[String] = None
               , startJobTime: Option[Instant] = None
               , endJobTime: Option[Instant] = None
               , error: Option[String] = None
               , createdAt: Instant = Instant.now()
               , updatedAt: Instant = Instant.now()
               , executedByUser: Option[Int] = None
               , ctxId: Option[Int] = None)


object Job {
  // noinspection TypeAnnotation
  implicit val configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val decoder: Decoder[Job] = deriveConfiguredDecoder
  implicit val encoder: Encoder[Job] = deriveConfiguredEncoder

//  def apply(id: Int=0, uuid: UUID, command: String): Job =
//    Job(id, uuid, command)

//  def apply(id: Int=0, uuid: UUID
//  def apply(id: Int=0, uuid: String
//            , command: String
//            , status: ExecutionJobStatus = ExecutionJobStatus.Queued
//            ,sparkJobId: Option[String]=None
//            , sparkJobAppName: Option[String]=None
//            , startJobTime: Option[Instant]=None
//            , endJobTime: Option[Instant]=None
//            , createdAt: Instant=Instant.now()
//            , updatedAt: Instant=Instant.now()
//            , executedByUser: Option[Int]=None
//            , ctxId: Option[Int]=None): Job =
//    Job(id, uuid, command, status, sparkJobId, sparkJobAppName, startJobTime, endJobTime, createdAt, updatedAt, executedByUser, ctxId)

//  def apply(id: Int=0, uuid: UUID, command: String, status: ExecutionJobStatus = ExecutionJobStatus.Queued
//            ,sparkJobId: Option[String]=None, sparkJobAppName: Option[String]=None
//            , startJobTime: Option[String]=None, endJobTime: Option[String]=None
//            createdAt: Option[String]=None, updatedAt: Option[String]=None
//            , executedByUser: Option[Int]=None, ctxId: Option[Int]=None): Job =
//    new Job(id, uuid, command, sparkJobId, sparkJobAppName, startJobTime, endJobTime, status, createdAt, updatedAt, executedByUser, ctxId)

//  def apply(
//             userId: Long,
////             recon: Recon,
//             jobUuid: UUID,
////             executionConf: ExecutionConf,
//             fromDate: LocalDate,
//             toDate: LocalDate
//           ): Job =
//    Job()
//    Job(
//      uuid = jobUuid,
////      reconId = recon.id,
//      executedByUser = userId,
////      executionConf = executionConf,
////      sparkJobAppName = executionConf.jobInfo.driverClass,
////      fromDate = fromDate,
////      toDate = toDate
//    )
}
