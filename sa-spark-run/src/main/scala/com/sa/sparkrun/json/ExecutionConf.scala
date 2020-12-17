package com.sa.sparkrun.json

import java.util.UUID

import cats.data.NonEmptyList
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}

final case class ExecutionConf(
//                                jobInfo: JobInfo,
//                                configFiles: ConfigFiles,
//                                inputs: Map[String, String],
//                                outputs: OutputInfo,
//                                dates: List[DateInfo],
                                matchCols: Map[String, List[String]],
                                aggCols: Map[String, List[String]],
//                                breaks: Option[NonEmptyList[String]],
//                                reconUuid: UUID,
                                jobUuid: UUID
                              )

object ExecutionConf {
  // noinspection TypeAnnotation
  implicit val configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val decoder: Decoder[ExecutionConf] =
    deriveConfiguredDecoder[ExecutionConf]

  implicit val encoder: Encoder[ExecutionConf] =
    deriveConfiguredEncoder[ExecutionConf]

  // TODO: Add factory function
  // TODO: Add dummy function for tests
}
