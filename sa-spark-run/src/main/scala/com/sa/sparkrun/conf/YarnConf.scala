package com.sa.sparkrun.conf

case class YarnConf(
                     clientConfig: Map[String, String],
                          javaPath: String,
                          javaHome: String,
                          sparkHome: String,
                          hadoopBase: String,
                          maxAttempts: Int,
                          defaultQueue: String,
                          connectionTimeout: Int,
                          classpath: String,
                          sparkJars: String,
                          yarnHadoop: List[String],
                          yarnSpark: List[String]
                        )

object YarnConf {
  val namespace: String = "yarnConf"
}
