//package com.sa.sparkrun.submit
//
//import cats.effect.{ContextShift, ExitCode, IO}
//import com.sa.sparkrun.params.SparkParamBuilderInstances.paramBuilder
//import fs2.Stream
//import org.http4s.Status.Successful
//import org.http4s.client.Client
//import org.http4s.{Header, Method, Request, Status, Uri}
////import org.http4s.circe._
//import org.http4s.client.blaze.BlazeClientBuilder
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import io.circe.generic.auto._
//import cats.Applicative.ops.toAllApplicativeOps
//import org.http4s.circe._
//import org.http4s.Status.{NotFound, Successful}
//import io.circe.generic.auto._
//
//final case class SpResponse(action: String, driverState: Option[String]
//                            , message: Option[String], serverSparkVersion: String
//                            , submissionId: String, success: Boolean)
//
//class StandaloneGet[F[_]] {
//
//  implicit val cs:ContextShift[IO] = IO.contextShift(global)
//
//  private def get[T](client: Client[IO], str: String): IO[Unit] = IO {
//    val dsu = Uri.fromString(str)
//
//    /*
//          {
//            "action" : "SubmissionStatusResponse",
//            "serverSparkVersion" : "2.4.0",
//            "submissionId" : "driver-20201116085227-0005",
//            "success" : false
//          }
//    */
//
//    println(s"INVOKING DRIVER RESPONSE 2nd APPROACH: ${dsu}")
//    //      val res2 = client.get(driverStatusUri) {
//    val res2 =
//      dsu match {
//        case Right(x) =>
//          client.get(x) {
//            case Successful(resp) =>  // decodeJson is defined for Json4s, Argonuat, and Circe, just need the right decoder!
//              println(s"${x} ${resp.decodeJson[SpResponse].unsafeRunSync()}")
//              resp.decodeJson[SpResponse]
////              resp.decodeJson[SpResponse].map("Received response: " + _)
//            case NotFound(_) =>
//              println(s"${str} NOT FOUND !!!")
//              IO.pure("Not Found!!!")
//            case resp =>
//              println(s"${str} FAILED: ${resp.status}")
//              IO.pure("Failed: " + resp.status)
//          }
//        case Left(ex) => throw  new Exception(s"BAD URI: ${ex.printStackTrace()}")
//
//      }
//    //      println(s"INVOKING DRIVER RESPONSE 2nd APPROACH AFTER unsafeToFuture")
//    res2.unsafeRunSync()
//    println("DONE DRIVER RESPONSE 2nd APPROACH AFTER unsafeRunSync")
//  }
//
////  def get(str: String): IO[ExitCode] =
////    BlazeClientBuilder[IO](global).resource.use(get(_,str)).as(ExitCode.Success)
//  def get(str: String, path:String): Stream[IO,Unit] =
//    for {
//      sparkParam <- paramBuilder.buildParam(path)
//      //      strm <- Stream.eval(BlazeClientBuilder[IO](global).resource.use(submit(_,sparkParam, str)).as(ExitCode.Success))
//      strm <- Stream.eval(BlazeClientBuilder[IO](global).resource.use(get(_,str)))
//      //    .resource.use(get(_,str)).as(ExitCode.Success)
//      //      .resource.use(submit(_,sparkParam)).as(ExitCode.Success)
//    } yield strm
//
//
//}
//
