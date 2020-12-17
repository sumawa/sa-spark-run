package com.sa.sparkrun.db.repository.doobie

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.{Currency, UUID}

import com.sa.sparkrun.db.models.ExecutionJobStatus
import com.sa.sparkrun.db.models.json.ExecutionResult.ExecutionResult

import io.circe.parser.decode
import io.circe.syntax._
import io.getquill.MappedEncoding

object DoobieMappingTypes {

  implicit val encodeCurrency: MappedEncoding[Currency, String] =
    MappedEncoding[Currency, String](_.getCurrencyCode)

  implicit val decodeCurrency: MappedEncoding[String, Currency] =
    MappedEncoding[String, Currency](Currency.getInstance)

  implicit val encodeUuid: MappedEncoding[UUID, String] =
    MappedEncoding[UUID, String](_.toString)

  implicit val decodeUuid: MappedEncoding[String, UUID] =
    MappedEncoding[String, UUID](UUID.fromString)

  implicit val encodeInstant: MappedEncoding[Instant, LocalDateTime] =
    MappedEncoding[Instant, LocalDateTime](LocalDateTime.ofInstant(_, ZoneOffset.UTC))

  implicit val decodeInstant: MappedEncoding[LocalDateTime, Instant] =
    MappedEncoding[LocalDateTime, Instant](_.toInstant(ZoneOffset.UTC))

//  implicit val encodeReconState: MappedEncoding[ReconState, String] =
//    MappedEncoding[ReconState, String](_.toString)
//
//  implicit val decodeReconState: MappedEncoding[String, ReconState] =
//    MappedEncoding[String, ReconState](ReconState.withNameInsensitive)
//
//  implicit val encodeExecutionConf: MappedEncoding[ExecutionConf, String] =
//    MappedEncoding[ExecutionConf, String](_.asJson.noSpaces)
//
//  // let's think about this situation
//  implicit val decodeExecutionConf: MappedEncoding[String, ExecutionConf] =
//    MappedEncoding[String, ExecutionConf](
//      decode[ExecutionConf](_)
//        .fold(
//          e => throw new RuntimeException(s"can't parse ExecutionConf: ${e.toString}"),
//          identity
//        )
//    )
//
//  implicit val encodeExecutionInput: MappedEncoding[ExecutionInput, String] =
//    MappedEncoding[ExecutionInput, String](_.toString)
//
//  implicit val decodeExecutionInput: MappedEncoding[String, ExecutionInput] =
//    MappedEncoding[String, ExecutionInput] { input =>
//      (for {
//        _ <- decode[ExecutionInput.ExampleValue](input).swap
//      } yield ExecutionInputError).merge
//    }

  implicit val encodeExecutionJobStatus: MappedEncoding[ExecutionJobStatus, String] =
    MappedEncoding[ExecutionJobStatus, String](_.toString)

  implicit val decodeExecutionJobStatus: MappedEncoding[String, ExecutionJobStatus] =
    MappedEncoding[String, ExecutionJobStatus](ExecutionJobStatus.withNameInsensitive)

//  implicit val encodeFileType: MappedEncoding[FileType, String] =
//    MappedEncoding[FileType, String](_.toString)
//
//  implicit val decodeFileType: MappedEncoding[String, FileType] =
//    MappedEncoding[String, FileType](FileType.withNameInsensitive)
//
//  implicit val encodeFileConf: MappedEncoding[FileConf, String] =
//    MappedEncoding[FileConf, String](_.asJson.noSpaces)
//
//  implicit val decodeFileConf: MappedEncoding[String, FileConf] =
//    MappedEncoding[String, FileConf] { input =>
//      (for {
//        _ <- decode[ConfigFile](input).swap
//        _ <- decode[FileSystem](input).swap
//      } yield FileConfError).merge
//    }
//
//  implicit val encodeSourceStatus: MappedEncoding[SourceProcessing.Status, String] =
//    MappedEncoding(_.toString)
//
//  implicit val decodeSourceStatus: MappedEncoding[String, SourceProcessing.Status] =
//    MappedEncoding(SourceProcessing.Status.withNameInsensitive)
//
//  implicit val encodeReconStatus: MappedEncoding[ReconProcessing.Status, String] =
//    MappedEncoding(_.toString)
//
//  implicit val decodeReconStatus: MappedEncoding[String, ReconProcessing.Status] =
//    MappedEncoding(ReconProcessing.Status.withNameInsensitive)
//
//  implicit val encodeConfValue: MappedEncoding[ConfValue, String] =
//    MappedEncoding[ConfValue, String](_.asJson.noSpaces)
//
//  implicit val decodeConfValue: MappedEncoding[String, ConfValue] =
//    MappedEncoding[String, ConfValue] { input =>
//      (for {
//        _ <- decode[GroupConfig](input).swap
//        _ <- decode[VariableConfig](input).swap
//      } yield ConfValueError).merge
//    }
//
//  implicit val encodeReconConf: MappedEncoding[Option[ReconConf], String] =
//    MappedEncoding(_.asJson.noSpaces)
//
//  implicit val decodeReconConf: MappedEncoding[String, Option[ReconConf]] =
//    MappedEncoding(
//      decode[Option[ReconConf]](_)
//        .fold(e => throw new RuntimeException(s"can't parse ReconConf: ${e.toString}"), identity)
//    )

  implicit val encodeExecutionResult: MappedEncoding[ExecutionResult, String] =
    MappedEncoding(_.asJson.noSpaces)

  implicit val decodeExecutionResult: MappedEncoding[String, ExecutionResult] =
    MappedEncoding(
      decode[ExecutionResult](_)
        .fold(_ => Map.empty[String, UUID], identity)
    )

//  implicit val encodeReconType: MappedEncoding[ReconType, String] =
//    MappedEncoding(_.toString)
//
//  implicit val decodeReconType: MappedEncoding[String, ReconType] = {
//    MappedEncoding(ReconType.withNameInsensitive)
//  }
}
