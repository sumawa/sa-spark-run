package com.sa.sparkrun.params

import cats.effect.IO
import com.sa.sparkrun.conf.{DaemonConf, YarnConf}
import fs2.Stream
import pureconfig.generic.auto._

sealed trait SparkParam[+A]
case class EnvVar(SP: Option[String] = Some("1"), SPARK_USER: Option[String] = Some("test"))

case class YarnParam(yarnConf: YarnConf, daemonConf: DaemonConf) extends SparkParam[YarnParam]

case class StandaloneParam(action: String,
                           appResource: String,
                           mainClass: String,
                           sparkProperties: Map[String, String],
                           clientSparkVersion: String = "2.4.0",
                           appArgs: List[String] ,
                           environmentVariables: EnvVar = EnvVar(Some("1"), Some("test")))

object StandaloneParam{
  val namespace: String = "standlone-param"
}
case class StandaloneConf(restHost: String
                         , standaloneParam: StandaloneParam) extends SparkParam[StandaloneConf]

object StandaloneConf{
  val namespace: String = "standaloneConf"
}

final case class SpResponse(action: String, driverState: Option[String]
                            , message: Option[String], serverSparkVersion: String
                            , submissionId: String, success: Boolean)

final case class SparkCommand(sparkClass: String
                             , applicationJar: String
                             , args: List[String])



