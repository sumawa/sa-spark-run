package com.sa.sparkrun.conf

//case class YarnClasspath()
case class YarnConf(
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
