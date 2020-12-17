package com.sa.sparkrun.submit

import enumeratum._

sealed trait AppStatus extends AnyRef with EnumEntry

object AppStatus extends Enum[AppStatus] {
  //noinspection TypeAnnotation
  def values = findValues

  case object New                                  extends AppStatus
  case object NewSaving                            extends AppStatus
  case object Submitted                            extends AppStatus
  case object Accepted                             extends AppStatus
  case object Running                              extends AppStatus
  case class Finished(diagnostics: Option[String]) extends AppStatus
  case object Succeeded                            extends AppStatus
  case class Failed(diagnostics: Option[String])   extends AppStatus
  case object Killed                               extends AppStatus
}
