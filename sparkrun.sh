SPARKRUN_ENV=dev SPARKRUN_HOME=$(pwd) SPARKRUN_TYPE=standalone java  -classpath ./sa-spark-run/target/scala-2.12/sa-spark-run-assembly-0.1.3.jar com.sa.sparkrun.SparkRunMain > output.log &
#SPARKRUN_ENV=dev SPARKRUN_HOME=$(pwd) SPARKRUN_TYPE=yarn java  -classpath ./sa-spark-run/target/scala-2.12/sa-spark-run-assembly-0.1.3.jar com.sa.sparkrun.SparkRunMain > output.log &