package com.sa.sparkrun.db.models

import enumeratum._

sealed trait ExecutionJobStatus extends EnumEntry

object ExecutionJobStatus extends Enum[ExecutionJobStatus] with CirceEnum[ExecutionJobStatus] {
//object ExecutionJobStatus extends Enum[ExecutionJobStatus] with QuillEnum[ExecutionJobStatus] {
  case object Submitted extends ExecutionJobStatus
  case object Running   extends ExecutionJobStatus
  case object Killed    extends ExecutionJobStatus
  case object Failed    extends ExecutionJobStatus
  case object Succeeded extends ExecutionJobStatus
  case object Queued    extends ExecutionJobStatus

  // noinspection TypeAnnotation
  val values = findValues

}


//import io.getquill.context.sql._
object ExecutionStatus extends Enumeration {

  type ExecutionStatus = Value

//  'Queued','NotStarted','Starting','Recovering','Idle','Running','Busy','ShuttingDown','Error','Dead','Killed','Success','SavedInDbQueue')
//  val Queued, NotStarted, Running, Unknown = Value
  val Queued,NotStarted,Starting,Recovering,Idle,Running,Busy,ShuttingDown,Error,Dead,Killed,Success,SavedInDbQueue = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(Queued)

//  // read from an Postgres ENUM and map to Enum in code
//  implicit val decoderSource: Decoder[ExecutionStatus1] = Circe.decoder((index, row) =>
//    ExecutionStatus1.withName(row.getObject(index).toString))
//
//  // write Enum code to a Postgres ENUM
//  implicit val encoderSource: Encoder[ExecutionStatus1] =
//    Circe.encoder(java.sql.Types.VARCHAR, (index, value, row) =>
//      row.setObject(index, value, java.sql.Types.OTHER)
}
