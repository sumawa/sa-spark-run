package com.sa.sparkrun.track

import cats.effect.{Async, Blocker, ContextShift, ExitCode, IO}
import com.sa.sparkrun.params.{SparkParam}
import com.sa.sparkrun.submit.{AppStatus}

class StandAloneMonitor[F[_]](blocker: Blocker)(implicit F: Async[F], blockingEc: ContextShift[F]){
  def monitor(appId: String): F[String] =
    blocker.blockOn(F.delay {
      throw new Exception("Not Implemented yet")
    })

//  def submit(app: String): F[SubmitResult] =
//    doSubmission(app).attemptT
//      .foldF(
//        e =>
//          logger.error(e)(s"can't create an application: ${e.getMessage}") *>
//            F.pure(SubmitResult.failure(e)),
//        appId =>
//          logger.info(s"successfully submitted: $appId") *>
//            F.pure(SubmitResult.success(appId, AppStatus.Submitted.toString))
//      ) // for now


  def run()(implicit  contextShift: ContextShift[IO]): F[String] = {

    import org.http4s._
    import cats.effect.IO
    import cats.syntax.functor._

    val req = Request[IO](Method.GET, Uri.uri("http://127.0.0.1:6066/v1/submissions/status/driver-20201116085227-0005"))


    import org.http4s.client.blaze.BlazeClientBuilder
    import scala.concurrent.ExecutionContext.Implicits.global


    println(s"########## LAUNCHING MONITOR ########## ${System.currentTimeMillis()}")
    val responseBody = BlazeClientBuilder[IO](global).resource.use(_.expect[String](req))
    val res = responseBody.flatMap(resp => IO(println(resp)))
    res.unsafeRunSync()

    println("Launched MONITOR ")
    F.pure("Monitored")
//    F.pure(SubmitResult.success("APPID",AppStatus.Submitted.toString))

//    http://127.0.0.1:18080/api/v1/applications
//    http://127.0.0.1:18080/api/v1/applications?status=RUNNING
  }

}

object StandAloneMonitorConstants {


  val SPARK_SUBMISSION_API_ACTION_CREATE = "CreateSubmissionRequest"
  val SPARK_MASTER_REST_PORT = "6066"
  val SPARK_MASTER_CLIENT_MODE_PORT = "7077"

  val SPARK_WEB_PORT = "8090"

  // Spark submission API end points
  val SPARK_API_URL_SUFFIX_APPLICATIONS = "/api/v1/applications"
  val SPARK_API_SUBMISSIONS_STATUS = "/v1/submissions/status/"
  val SPARK_API_SUBMISSIONS_CREATE = "/v1/submissions/create/"
  val SPARK_API_SUBMISSIONS_KILL = "/v1/submissions/kill/"

  // Spark application monitoring API end points
  val SPARK_API_APP_KILL = "/app/kill/"
  val SPARK_API_APP_RUNNING = "/api/v1/applications/?status=RUNNING"

  val SPARK_DRIVER_STATUS_SUBMITTED = "SUBMITTED"
  val SPARK_DRIVER_STATUS_KILLED = "KILLED"
  val SPARK_DRIVER_STATUS_RUNNING = "RUNNING"
  val SPARK_DRIVER_STATUS_FINISHED = "FINISHED"
  val SPARK_DRIVER_STATUS_FAILED = "FAILED"
  val SPARK_DRIVER_STATUS_ERROR = "ERROR"

  // prepare Spark App / Submission Urls using params
  def getSparkAppUrl(sparkIp: String, sparkPort: String) = s"http://${sparkIp}:${sparkPort}${SPARK_API_URL_SUFFIX_APPLICATIONS}"
  def getSparkAppKillUrl(sparkIp: String, sparkWebPort: String) = s"http://${sparkIp}:${sparkWebPort}${SPARK_API_APP_KILL}"
  def getSparkAppRunningUrl(sparkIp: String, sparkPort: String) = s"http://${sparkIp}:${sparkPort}${SPARK_API_APP_RUNNING}"

  def getSparkSubmissionCreateUrl(sparkIp: String, sparkSubmissionPort: String) = s"http://${sparkIp}:${sparkSubmissionPort}${SPARK_API_SUBMISSIONS_CREATE}"
  def getSparkSubmissionStatusUrl(sparkIp: String, sparkSubmissionPort: String, submissionId: String) = s"http://${sparkIp}:${sparkSubmissionPort}${SPARK_API_SUBMISSIONS_STATUS}${submissionId}"
  def getSparkSubmissionKillUrl(sparkIp: String, sparkSubmissionPort: String, submissionId: String) = s"http://${sparkIp}:${sparkSubmissionPort}${SPARK_API_SUBMISSIONS_KILL}${submissionId}"

  def getSparkSubmissionLogUrl(sparkIp: String, workerPort: String, submissionId: String, logType: String) = s"http://${sparkIp}:${workerPort}/logPage/?driverId=${submissionId}&logType=${logType}"

}
