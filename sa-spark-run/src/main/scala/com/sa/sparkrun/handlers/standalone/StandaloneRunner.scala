package com.sa.sparkrun.handlers.standalone

import cats.data.EitherT
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.handlers.{MySparkRunner, SpSuccess, StandaloneRunParam}
import com.sa.sparkrun.params.{SpResponse, SparkCommand}
import com.sa.sparkrun.submit._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{Header, Method, Request, Uri}

class StandaloneRunner[F[_]](myStandaloneRunParam: StandaloneRunParam) extends MySparkRunner [F]{
  // TODO: use alias
  type EitherSP[A] = EitherT[F,String,A]

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val cs:ContextShift[IO] = IO.contextShift(global)

  import org.http4s.circe.CirceEntityEncoder._
  import cats.data.EitherT
  import io.circe.syntax._
  import io.circe.generic.auto._

  def submit(job:Job)
                    (implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess] = {

    println(s"######## LAUNCHING NEW STANDALONE RUNNER SPARK SUBMIT ######## :: ${System.currentTimeMillis()}")
    val str = myStandaloneRunParam.standaloneConf.restHost + "/v1/submissions/create"
    for {
      uri <- parseUri(str)
      sparkCommand <- parseSparkCommand(job.command)
      defaultStandaloneParam = myStandaloneRunParam.standaloneConf.standaloneParam
      standaloneParam = defaultStandaloneParam.copy(
        appResource = sparkCommand.applicationJar
        , mainClass = sparkCommand.sparkClass
        , appArgs = sparkCommand.args
      )
      req = Request[F](Method.POST, uri)
        .withHeaders(Header("Content-Type","application/json"))
        .withEntity(standaloneParam.asJson)
      resp <- processReq(req)
    } yield (resp)
  }

  import cats.syntax.functor._
  def processReq(req: Request[F]) (implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess] = {
    import org.http4s.circe.CirceEntityDecoder._
    scala.util.Try(BlazeClientBuilder(global).resource.use(_.expect[SpResponse](req))) match {
      case scala.util.Success(x) => EitherT.right[String] ( for {
        spResponse <- x
      } yield(SpSuccess(spResponse.submissionId, AppStatus.Submitted))  )
      case scala.util.Failure(ex) =>
        EitherT.left[SpSuccess](F.delay(s"----- FAILED TO PROCESS STANDALONE SUBMIT: ${ex.getMessage} -----"))
    }
  }
  def parseSparkCommand(sparkCommandStr: String)
                 (implicit F: ConcurrentEffect[F]): EitherT[F,String,SparkCommand] = {
    import io.circe.generic.auto._
    import io.circe.parser.decode

    decode[SparkCommand](sparkCommandStr) match {
      case Right(sc) => EitherT.right(F.delay(sc))
      case Left(ex) =>
        F.delay(println(s"Errror parsing String to SparkCommand: ${ex.getMessage}"))
        EitherT.left(F.delay(s"Errror parsing String to SparkCommand: ${ex.getMessage}"))
    }
  }

  def parseUri(str: String)
              (implicit F: ConcurrentEffect[F]): EitherT[F,String,Uri] =
    Uri.fromString(str) match {
      case Right(uri) => EitherT.right(F.delay(uri))
      case Left(ex) => EitherT.left(F.delay(s"Error parsing uri: ${ex.getMessage()}"))
    }
}
