package com.sa.sparkrun

import cats.effect.{Blocker, ContextShift, ExitCode, IOApp}
import com.sa.sparkrun.conf._
import com.sa.sparkrun.handlers.{SparkClientHelper, SparkRunnerHelper}
import com.sa.sparkrun.source.JobServiceHelper
import com.sa.sparkrun.submit.SparkLauncher
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.apache.hadoop.conf.Configuration

object SparkRunMain extends IOApp {

  // THIS will help with config
  // https://github.com/lightbend/config

  import cats.effect.IO

  import cats.effect.{IO, Timer}
  import cats.implicits._

  import com.sa.sparkrun.conf.ConfHelper.{loadCnfF,loadCnfDefault}

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val T: Timer[IO] = IO.timer(global)

  import java.nio.file.Paths

//  import com.sa.sparkrun.params.SparkParamBuilderInstances._

  val hadoopConf = new Configuration

  import com.sa.sparkrun.submit.yarn.YarnClientHelper

  import pureconfig.generic.auto._

  private def init(blocker: Blocker) = for {
    logger <- Stream.eval(Slf4jLogger.create[IO])

    envConfig <- loadCnfDefault[IO, EnvConfig](EnvConfig.namespace,blocker)
    externalConfigPath = Paths.get(envConfig.getExternalConfigPath)
    _ <- Stream.eval(IO(println(s"externalConfigPath = ${externalConfigPath}")))
    sparkType = envConfig.sparkType
    _ <- Stream.eval(IO(println(s"sparkType = ${sparkType}")))

    sparkRunner <- SparkRunnerHelper.loadRunner[IO](sparkType, externalConfigPath, blocker)
    _ <- Stream.eval(IO(println(s"sparkRunner = ${sparkRunner}")))

    jobS <- JobServiceHelper.loadSource[IO]("sql",externalConfigPath,blocker)

    launcher = new SparkLauncher[IO](sparkRunner, logger, blocker)

    _ <- Stream.eval(IO{
      println(s"----- DOING TEST SUBMIT ----- ")
      launcher.run(jobS).value.unsafeRunSync()
    })
//    sparkRunIO = logE(IO{launcher.run(jobR).unsafeRunSync()})(logger)
//    stream         <- Stream.awakeEvery[IO](10 seconds) >> Stream.eval((sparkRunIO,IO.unit).tupled.void)
  } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use(init(_).compile.drain)
      .handleError(e => println(s"BOOTSTRAP SPARK RUN ERROR OCCURRED: \n\t ${e}"))
      .as(ExitCode.Error)

}
