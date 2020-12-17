//package com.sa.sparkrun
//
//import java.nio.file.Paths
//
//import cats.effect.{Blocker, ContextShift, IO, Timer}
//import com.sa.sparkrun.conf.{DatabaseConfig, EnvConfig}
//import com.sa.sparkrun.db.{DoobieJobRepository, PooledTransactor}
//import com.sa.sparkrun.db.domain.job.Job
//import com.sa.sparkrun.handlers.SparkRunnerHelper
//import com.sa.sparkrun.params.SparkCommand
//import com.sa.sparkrun.service.JobServiceImpl
//import fs2.Stream
//import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
//import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}
//import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
//import pureconfig.module.catseffect.loadConfigF
//import pureconfig.generic.auto._
//
////trait BaseSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaCheckDrivenPropertyChecks {
//trait BaseSpec extends FlatSpec with Matchers with BeforeAndAfter with ScalaCheckDrivenPropertyChecks {
//  implicit val glo = scala.concurrent.ExecutionContext.Implicits.global
//  implicit val cs: ContextShift[IO] = IO.contextShift(glo)
//  implicit val timer: Timer[IO] = IO.timer(glo)
//
//  System.setProperty("SPARKRUN_ENV","test")
//  System.setProperty("SPARKRUN_TYPE","yarn")
//
////  val JOB_TEST_UUID1 = java.util.UUID.randomUUID().toString
////  val JOB_TEST_UUID2 = java.util.UUID.randomUUID().toString
//
//  val yarnCommandCorrectPath =
//    SparkCommand("org.apache.spark.examples.SparkPi","hdfs:///sparkrun/books/spark-examples_2.11-2.4.0.jar",List("80"))
//  val yarnCommandWrongPath =
//    SparkCommand("org.apache.spark.examples.SparkPi","hdfs:///opt/books1/spark-examples_2.11-2.4.0.jar",List("80"))
//
//  import io.circe.generic.auto._
//  import io.circe.syntax._
//
//  import java.util.UUID._
//  val correctJob = Job(0,randomUUID().toString,yarnCommandCorrectPath.asJson.toString)
//  val wrongPathJob = Job(0,randomUUID().toString,yarnCommandWrongPath.asJson.toString)
//
//  val dbResourcesTuple = (for {
//    logger <- Stream.eval(Slf4jLogger.create[IO])
//    envConfig <- Stream.eval(loadConfigF[IO, EnvConfig](EnvConfig.namespace))
//    externalConfigPath = Paths.get(envConfig.getExternalConfigPath)
//    databaseConf <- loadCnf[DatabaseConfig](externalConfigPath, DatabaseConfig.namespace)
//    _ <- Stream.eval(IO(println(s"got databaseConf: ${databaseConf}")))
//    xa <- Stream.eval(PooledTransactor[IO](databaseConf))
//    _ <- Stream.eval(IO(println(s"GOT ### XA: ${xa}")))
//    jobR           = new DoobieJobRepository[IO](xa)
//    _ <- Stream.eval(IO(println(s"GOT ### DoobieJobRepository: ${jobR}")))
//    jobS = new JobServiceImpl[IO](jobR)
//  } yield (xa,jobS)).compile.toList.unsafeRunSync()(0)
//
//  val (xa,jobS) = (dbResourcesTuple._1,dbResourcesTuple._2)
//
//  before {
//    jobS.insert(correctJob).unsafeRunSync()
//    jobS.insert(wrongPathJob).unsafeRunSync()
//  }
//   after {
//     println(s"CLEANING UP TEST DB &&&&&&&&&&&&&&")
////     Thread.sleep(10000)
//     jobS.deleteByUUID(correctJob.uuid).unsafeRunSync()
//     jobS.deleteByUUID(wrongPathJob.uuid).unsafeRunSync()
//   }
//
//  def getSparkRunner(blocker: Blocker) =
//    for {
//      logger <- Stream.eval(Slf4jLogger.create[IO])
////      _ <- Stream.eval(System.setProperty("SPARKRUN_TYPE",runType))
//      envConfig <- Stream.eval(loadConfigF[IO, EnvConfig](EnvConfig.namespace))
//      externalConfigPath = Paths.get(envConfig.getExternalConfigPath)
//      _ <- Stream.eval(IO(println(s"externalConfigPath: ${externalConfigPath}")))
//      sparkType = envConfig.sparkType
//      _ <- Stream.eval(IO(println(s"sparkType = ${sparkType}")))
//      sparkRunner <- SparkRunnerHelper.loadRunner[IO](sparkType, externalConfigPath, blocker)
//      _ <- Stream.eval(IO(println(s"sparkRunner = ${sparkRunner}")))
////  } yield (sparkRunner)).compile.toList.unsafeRunSync()(0)
//    } yield (sparkRunner)
//
//  def yarnSparkRunner = (Blocker[IO].use(b => getSparkRunner(b).compile.toList)).unsafeRunSync()(0)
////  val standaloneSparkRunner = (Blocker[IO].use(b => getSparkRunner(b, "standalone")))
//
//
//
//
//}