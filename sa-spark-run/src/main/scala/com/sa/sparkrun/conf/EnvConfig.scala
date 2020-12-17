package com.sa.sparkrun.conf

case class EnvConfig(activeEnv: String, homeDir: String, triggerInterval: Int, sparkType: String) {

  def getExternalConfigPath: String =
    {println("GETTING EXTERNAL"); s"${this.homeDir}/${this.activeEnv}.conf".toLowerCase}

  def getApplicationConfPath: String = s"${this.homeDir}/application.conf".toLowerCase
  def getSparkType: String = s"${this.sparkType}".toLowerCase
}

object EnvConfig {
  val namespace: String = "env"
}
