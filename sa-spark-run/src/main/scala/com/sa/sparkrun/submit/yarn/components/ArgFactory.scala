package com.sa.sparkrun.submit.yarn.components

import com.sa.sparkrun.conf.SubmitterConf

case class Resources(memory: Int, cores: Int, executors: Int)

case class Application(
                        appName: String,
                        jarPath: String,
                        className: String,
                        resources: Resources,
                        queueName: Option[String],
                        args: Seq[String]
                      )

class ArgFactory(conf: SubmitterConf) {

  def fromApp(app: Application): List[String] =
    collectAppDetails(app) ++ configureLogging

  private def collectAppDetails(app: Application) =
    List(
      conf.javaPath,
      "-server -Xmx3g",
      "org.apache.spark.deploy.yarn.ApplicationMaster",
      s"--class '${app.className}'",
      s"--jar __app__.jar" // alias, the real jar location is set through local resource mapping,
    ) ++ app.args.map(a => s"--arg $a")

  private def configureLogging = List("1><LOG_DIR>/stdout 2><LOG_DIR>/stderr")
}
