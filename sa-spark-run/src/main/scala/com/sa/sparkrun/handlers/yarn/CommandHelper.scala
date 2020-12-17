package com.sa.sparkrun.handlers.yarn

import com.sa.sparkrun.params.YarnParam

object CommandHelper {

  def fromApp(yarnParam: YarnParam, app: Application): List[String] =
    collectAppDetails(yarnParam, app) ++ configureLogging

  private def collectAppDetails(yarnParam: YarnParam, app: Application) =
    List(
      yarnParam.yarnConf.javaPath
      , "-server -Xmx2g"
      , "org.apache.spark.deploy.yarn.ApplicationMaster"
      , s"--class 'org.apache.spark.examples.SparkPi'"
      , s"--properties-file /opt/spark-2.4.0-bin-hadoop2.7/spark-properties.conf"
//      , s"--conf spark.app.name=SPARK_RUN"
      , s"--jar __app__.jar" // alias, the real jar location is set through local resource mapping,
      //      s"--jar hdfs:///sparkrun/books/spark-examples_2.11-2.4.0.jar" // alias, the real jar location is set through local resource mapping,
    ) ++ app.args.map(a => s"--arg $a")

  private def configureLogging = List("1><LOG_DIR>/stdout 2><LOG_DIR>/stderr")

}
