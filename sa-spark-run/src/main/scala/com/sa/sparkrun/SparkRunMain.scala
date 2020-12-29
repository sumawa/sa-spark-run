package com.sa.sparkrun

import cats.effect.{Blocker, ContextShift, ExitCode, IOApp}
import com.sa.sparkrun.conf._
import com.sa.sparkrun.handlers.{SparkClientHelper, SparkRunner}
import com.sa.sparkrun.source.JobServiceHelper
import com.sa.sparkrun.submit.SparkLauncher
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.apache.hadoop.conf.Configuration
import cats.effect.{IO, Timer}
import cats.implicits._
import com.sa.sparkrun.conf.ConfHelper.{loadCnfDefault, loadCnfF}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object SparkRunMain extends IOApp {

  // THIS will help with config
  // https://github.com/lightbend/config


  implicit val T: Timer[IO] = IO.timer(global)

  import java.nio.file.Paths

  val hadoopConf = new Configuration

  /*
    Do not remove this import required by pure config auto derivation.
    As per documentation:
    Automatic reader derivation is used throughout all the documentation pages.
    It is activated simply by importing pureconfig.generic.auto._
    everywhere readers are needed (for example, where ConfigSource#load is used):
    */
  import pureconfig.generic.auto._

  import com.sa.sparkrun.SparkRunImplicits._
  private def init(blocker: Blocker): Stream[IO,Unit] = for {
    logger <- Stream.eval(Slf4jLogger.create[IO])

    envConfig <- loadCnfDefault[IO, EnvConfig](EnvConfig.namespace,blocker)
    externalConfigPath = Paths.get(envConfig.getExternalConfigPath)
    _ <- Stream.eval(IO(println(s"externalConfigPath = ${externalConfigPath}")))
    sparkType = envConfig.sparkType
    _ <- Stream.eval(IO(println(s"sparkType = ${sparkType}")))

    sparkRunner <- SparkRunner.loadRunner[IO](sparkType, externalConfigPath, blocker)
    _ <- Stream.eval(IO(println(s"sparkRunner = ${sparkRunner}")))

    jobS <- JobServiceHelper.loadSource[IO]("sql",externalConfigPath,blocker)

    launcher = new SparkLauncher[IO](sparkRunner, jobS, logger, blocker)

    res = launcher.run()
    stream <- Stream.eval(triggerEither(res))
    //    stream <- Stream.awakeEvery[IO](10 seconds) >> Stream.eval((sparkRunIO,IO.unit).tupled.void)
//      println(s"----- DOING TEST SUBMIT ----- ")
  } yield (stream)

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use(init(_).compile.drain)
      .handleError(e => println(s"BOOTSTRAP SPARK RUN ERROR OCCURRED: \n\t ${e}"))
      .as(ExitCode.Error)
}
