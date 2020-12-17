//package com.sa.sparkrun
//
//import cats.effect.{Blocker, ExitCode, IOApp}
//import com.sa.sparkrun.conf._
//import com.sa.sparkrun.db.{DoobieJobRepository, PooledTransactor}
//import com.sa.sparkrun.submit.Launcher
//import com.sa.sparkrun.submit.yarn.{ApplicationFactory, NewYarnSubmitter, YarnRun}
//import io.chrisdavenport.log4cats.StructuredLogger
//import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
//import org.apache.hadoop.conf.Configuration
//import pureconfig.{ConfigReader, Derivation}
//
//import scala.reflect.ClassTag
//import fs2.Stream
//import com.sa.sparkrun.conf.ConfHelper.loadCnf
//import com.sa.sparkrun.params.YarnParam
//import pureconfig.module.catseffect.loadConfigF
//import pureconfig.generic.auto._
//import scratch.DummyYarnClient
//
//object FinalSparkMain extends IOApp {
//
//  import cats.effect.IO
//
//  private def logE(ioa: IO[_])(logger: StructuredLogger[IO]): IO[Unit] =
//    ioa.attempt.flatMap(et => et.fold(ex => logger.error(ex)(ex.getMessage), _ => IO.unit))
//
//  import cats.effect.{IO, Timer}
//  import cats.implicits._
//
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import scala.concurrent.duration._
//  implicit val T: Timer[IO] = IO.timer(global)
//
//  import java.nio.file.Paths
//
//  import com.sa.sparkrun.params.SparkParamBuilderInstances._
//
//  val hadoopConf = new Configuration
//
//  import com.sa.sparkrun.submit.yarn.YarnClientHelper
//  private def init(blocker: Blocker) = for {
//    logger <- Stream.eval(Slf4jLogger.create[IO])
//    envConfig <- Stream.eval(loadConfigF[IO, EnvConfig](EnvConfig.namespace))
//    externalConfigPath = Paths.get(envConfig.getExternalConfigPath)
//    sparkParam <- paramBuilder.buildParam(envConfig.getExternalConfigPath)
//
//    yarnConf <- loadCnf[YarnConfig](externalConfigPath,YarnConfig.namespace)
//    submitterConf <- loadCnf[SubmitterConf](externalConfigPath, SubmitterConf.namespace)
//
//    daemonConf <- loadCnf[DaemonConf](externalConfigPath, DaemonConf.namespace)
//    yarnParam = YarnParam(yarnConf, submitterConf, daemonConf)
//    appFactory = new ApplicationFactory[IO](logger, submitterConf, daemonConf)
//
//    yarnClient <- Stream.eval(YarnClientHelper.build[IO](yarnConf.clientConfig))
//    yarnRun <- Stream.eval(IO(YarnRun.makeRunner[IO](yarnClient)))
//    databaseConf <- loadCnf[DatabaseConfig](externalConfigPath, DatabaseConfig.namespace)
//    _ <- Stream.eval(IO(println(s"got databaseConf: ${databaseConf}")))
//    xa <- Stream.eval(PooledTransactor[IO](databaseConf))
//    _ <- Stream.eval(IO(println(s"GOT ### XA: ${xa}")))
//    jobR           = new DoobieJobRepository[IO](xa)
//    _ <- Stream.eval(IO(println(s"GOT ### DoobieJobRepository: ${jobR}")))
//    sparkCreateUrl <- Stream.eval(IO.pure("http://127.0.0.1:6066/v1/submissions/create"))
//
//    launcher = new Launcher[IO](sparkCreateUrl, sparkParam, yarnClient, yarnParam
//      , yarnRun, appFactory, logger, blocker)
//    _ <- Stream.eval(IO{
//      println(s"----- DOING TEST SUBMIT ----- ")
//      launcher.run(jobR)
//    })
//    sparkRunIO = logE(IO{launcher.run(jobR)})(logger)
//    stream         <- Stream.awakeEvery[IO](10 seconds) >> Stream.eval((sparkRunIO,IO.unit).tupled.void)
//  } yield stream
//
////  override def run(args: List[String]): IO[ExitCode] =
////      init().compile.drain
////        .handleError(e => println(s"BOOTSTRAP SPARK RUN ERROR OCCURED: \n\t ${e}"))
////        .as(ExitCode.Error)
//
//  override def run(args: List[String]): IO[ExitCode] =
//    Blocker[IO].use(init(_).compile.drain)
//      .handleError(e => println(s"BOOTSTRAP SPARK RUN ERROR OCCURED: \n\t ${e}"))
//      .as(ExitCode.Error)
//
//}
