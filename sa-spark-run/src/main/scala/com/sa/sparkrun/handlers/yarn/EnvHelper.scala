package com.sa.sparkrun.handlers.yarn

import com.sa.sparkrun.conf.SubmitterConf

case class Resources(memory: Int, cores: Int, executors: Int)

object EnvHelper {

  def build(conf: SubmitterConf): Map[String, String] =
    Map(
      "JAVA_HOME"            -> conf.javaHome, //"/home/tdss/downloads/jdk1.8.0_201/",
      "SPARK_YARN_MODE"      -> "true",
//      "CLASSPATH"            -> conf.classpath,
      "CLASSPATH"            -> classpathBuilder(conf),
      "SPARK_HOME"           -> conf.sparkHome, // "/opt/mapr/spark/spark-2.3.2/"
      "SPARK_DIST_CLASSPATH" -> conf.sparkJars
    )

  def classpathBuilder(conf: SubmitterConf) = {
    val classpathPrefix = conf.classpath
    val classpathSpark = conf.yarnSpark.map(entry => s"${conf.sparkHome}/${entry}").mkString("<CPS>")
    val classpathHadoop = conf.yarnHadoop.map(entry => s"${conf.hadoopBase}/${entry}").mkString("<CPS>")
    val finalClasspath = classpathPrefix  +  classpathSpark + "<CPS>" + classpathHadoop
    println(s"FINAL CLASSPATH: ${finalClasspath}")
    finalClasspath
  }
}
