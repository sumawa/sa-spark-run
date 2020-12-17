package com.sa.sparkrun.handlers.yarn

import com.sa.sparkrun.conf.{DaemonConf, YarnConf}
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.params.SparkCommand

case class Application(
                        appName: String,
                        jarPath: String,
                        className: String,
                        resources: Resources,
                        queueName: Option[String],
                        args: Seq[String]
                      )

object ApplicationHelper{
  def buildApp(jobInstance: Job, daemonConf: DaemonConf, yarnConf: YarnConf): Application = {
    val sparkCommand = buildSparkCommand(jobInstance)
    Application(
      appName = jobInstance.sparkJobAppName.fold("DEFAULT")(_.toString),
      jarPath = sparkCommand.applicationJar,
//      jarPath = "hdfs:///opt/books1/spark-examples_2.11-2.4.0.jar",   // for error testing
//      jarPath = "hdfs:///sparkrun/books/spark-examples_2.11-2.4.0.jar",   // for error testing
      className = sparkCommand.sparkClass,
      resources = Resources(daemonConf.memory, daemonConf.cores, daemonConf.executors),
      queueName = Some(yarnConf.defaultQueue),
      //  args = Seq("80") ++ Seq(daemonConf.sparkParams)
      //        args = Seq(encodeExecCnf(jobInstance.executionConf)) ++ Seq(daemonConf.sparkParams) ++ Seq(
      args = sparkCommand.args.toSeq //TODO: Put baseDir in yarnConf and pass that context here
    )
  }

  import io.circe.generic.auto._
  import io.circe.parser.decode
  def buildSparkCommand(job: Job): SparkCommand = {
    val sparkCommandStr = job.command

    decode[SparkCommand](sparkCommandStr) match {
      case Right(sc) => sc
      case Left(ex) =>
        println(s"Errror parsing String to SparkCommand: ${ex.getMessage}")
        SparkCommand("","",List(""))
    }
  }


}
