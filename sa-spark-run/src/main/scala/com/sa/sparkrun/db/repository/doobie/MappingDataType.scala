package com.sa.sparkrun.db.repository.doobie

import java.util.UUID

import doobie.util._
import _root_.io.circe.parser.decode
import _root_.io.circe.syntax._
import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.db.models.json.ExecutionResult.ExecutionResult

object MappingDataType {

  implicit val executionResultRead: Read[ExecutionResult] = {
    Read[String].map(decode[ExecutionResult](_).fold(throw _, identity))
  }

  implicit val executionResultWrite: Write[ExecutionResult] =
    Write[String].contramap(_.asJson.noSpaces)

//  implicit val reconProcessingStatusRead: Read[ReconProcessing.Status] = {
//    Read[String].map(ReconProcessing.Status.withNameInsensitive)
//  }

//  implicit val reconProcessingStatusWrite: Write[ReconProcessing.Status] =
//    Write[String].contramap(_.toString)

  implicit val uuidRead: Read[UUID]   = Read[String].map(UUID.fromString)
  implicit val uuidWrite: Write[UUID] = Write[String].contramap(_.toString)

  implicit val uuidGet: Get[UUID] = Get[String].map(UUID.fromString)
  implicit val uuidPut: Put[UUID] = Put[String].contramap(_.toString)

  implicit val executionJobStatusMeta: Meta[ExecutionJobStatus] =
    Meta[String].imap(ExecutionJobStatus.withName)(_.entryName)

//  implicit val fileTypeMeta: Meta[FileType] =
//    Meta[String].imap(FileType.withName)(_.entryName)
//
//  implicit val reconStateMeta: Meta[ReconState] =
//    Meta[String].imap(ReconState.withName)(_.entryName)
//
//  implicit val confValueWrite: Write[ConfValue] =
//    Write[String].contramap(_.toString)

  //TODO: Need to change from here
//  implicit val executionInputRead: Read[ExecutionInput] =
//    Read[String].map(_ => ExecutionInputError)
//
//  implicit val executionInputWrite: Write[ExecutionInput] =
//    Write[String].contramap(_.toString)
//
//  //TODO: Need to change from here
//  implicit val fileConfRead: Read[FileConf] =
//    Read[String].map(decode[FileConf](_).fold(throw _, identity))
//
//  implicit val fileConfWrite: Write[FileConf] =
//    Write[String].contramap(_.asJson.noSpaces)
//
//  implicit val executionConfRead: Read[ExecutionConf] =
//    Read[String].map(decode[ExecutionConf](_).fold(throw _, identity))
//
//  implicit val executionConfWrite: Write[ExecutionConf] =
//    Write[String].contramap(_.asJson.noSpaces)
//
//  implicit val breakmetricStatusRead: Read[BreakMetricStatus] =
//    Read[String].map(BreakMetricStatus.withNameInsensitive)
//
//  implicit val breakmetricStatusWrite: Write[BreakMetricStatus] =
//    Write[String].contramap(_.entryName)
//
//  implicit val bmsMeta: Meta[BreakMetricStatus] =
//    Meta[String].timap(BreakMetricStatus.withNameInsensitive)(_.entryName)
//
//  implicit val reconStateRead: Read[ReconState] =
//    Read[String].map(decode[ReconState](_).fold(throw _, identity))
//
//  implicit val reconStateWrite: Write[ReconState] =
//    Write[String].contramap(_.asJson.noSpaces)

//  implicit val bookMeta: Meta[Book] = Meta[String].timap(Book.apply)(_.name)
//  implicit val rdmMeta: Meta[RDM]   = Meta[String].timap(RDM.apply)(_.modelName)
//
//  implicit val tcurrencyMeta: Meta[TCurrency] =
//    Meta[String].timap(TCurrency.withNameInsensitive)(_.entryName)
//
//  implicit val breakTypeMeta: Meta[BreakType] = Meta[String].timap(BreakType.apply)(_.name)
}
