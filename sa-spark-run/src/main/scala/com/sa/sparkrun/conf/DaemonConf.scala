package com.sa.sparkrun.conf

case class DaemonConf(
                       cores: Int,
                       memory: Int,
                       executors: Int,
                       jarPath: String,
                       sparkParams: String,
                       httpParams: String,
                       baseDir: String,
                       jdbcParams: String,
                       assemblyPath: String,
                       periodicity: Int // in seconds
                     )

object DaemonConf {
  val namespace: String = "daemon"
}