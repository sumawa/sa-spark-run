//package com.sa.sparkrun.submit
//
//import java.nio.file.Path
//import java.util.UUID
//
//import cats.effect.{Effect, Timer}
//import com.sa.sparkrun.params.SparkParamBuilderInstances.paramBuilder
//
////case class Resources(memory: Int, cores: Int, executors: Int)
////
////case class Application(
////  appName: String,
////  jarPath: String,
////  className: String,
////  resources: Resources,
////  queueName: Option[String],
////  args: Seq[String]
////)
//
//sealed trait SubmitResult {
//  sr =>
//  def fold[A](onError: Throwable => A, onSuccess: (String, AppStatus) => A): A =
//    sr match {
//      case Success(appId: String, status: AppStatus) => onSuccess(appId, status)
//      case Failure(err)                             => onError(err)
//    }
//}
//
//case class Success(appId: String, status: AppStatus)   extends SubmitResult
//case class Failure(err: Throwable)                  extends SubmitResult
//
//object SubmitResult {
//  def success(appId: String, status: AppStatus): SubmitResult = Success(appId, status)
//  def failure(exception: Throwable): SubmitResult            = Failure(exception)
//}
//
//import cats.effect.IO
//import fs2.Stream
//
//trait SparkSubmitRunner[A]{
//  def sparkRunner(path: java.nio.file.Path): StandaloneSubmit[IO]
//}
//
///**
//  * @tparam F Effect
//  */
//trait SparkSubmit[F[_]] {
////  def submit(app: String): F[SubmitResult]
////  def run(url: String): F[SubmitResult]
//  def run(url: String): Stream[IO,SpResponse]
//}
//
//object SparkSubmitRunnerInstances {
//  implicit val standaloneRunner: SparkSubmitRunner[StandaloneSubmit[IO]] =
//    new SparkSubmitRunner[StandaloneSubmit[IO]] {
//
//      override def sparkRunner(path: Path): StandaloneSubmit[IO] =
//        new StandaloneSubmit[IO]()
//    }
//}
////import cats.effect.{IO}
////trait Submitter[F[_]] {
////  def submit(app: String): IO[String]
////}
//
////class DummySubmitter[F[_]](implicit F: Effect[F], timer: Timer[F]) extends Submitter[F] {
////  import cats.implicits._
////
////  import scala.concurrent.duration._
////
////  def submit(app: Application): F[SubmitResult] =
////    timer.sleep(math.abs(scala.util.Random.nextInt(3)).seconds) *>
////      F.pure(SubmitResult.success(UUID.randomUUID.toString, AppStatus.Running))
////}
