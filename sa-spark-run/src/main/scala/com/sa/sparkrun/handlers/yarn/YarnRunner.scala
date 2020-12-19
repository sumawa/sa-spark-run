package com.sa.sparkrun.handlers.yarn

import cats.data.EitherT
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import com.sa.sparkrun.conf.ConfHelper.loadCnfF
import com.sa.sparkrun.conf.{DaemonConf, YarnConf}
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.handlers.{SparkRunner, MyYarnRunParam, SpSuccess}
import com.sa.sparkrun.params.YarnParam
import com.sa.sparkrun.submit._
import com.sa.sparkrun.submit.yarn.YarnClientHelper
import fs2.Stream
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.{YarnClient, YarnClientApplication}
import org.apache.hadoop.yarn.util.Records

class YarnRunner[F[_]](myYarnRunParam: MyYarnRunParam) extends SparkRunner [F]{

  import cats.effect.ConcurrentEffect
  import cats.implicits._

  import scala.collection.JavaConverters._

  def handleSubmit[T](t: => T)
                     (implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess] =
    scala.util.Try (t) match {
      case scala.util.Success(res) =>
        F.delay(println(s"HANDLE SUBMIT ---- ${res} "))
        EitherT.rightT[F,String](SpSuccess(res.toString,AppStatus.Submitted))
      case scala.util.Failure(ex) =>
        EitherT.leftT[F,SpSuccess](s"----- FAILED TO HANDLE SUBMIT: ${ex.getMessage} -----")
    }

//  import cats.implicits._
  def submit(job:Job)
                    (implicit F: ConcurrentEffect[F]): EitherT[F,String,SpSuccess] = {

    println(s"%%%%%%% YARNRUNNER SUBMIT %%%%%%%%%%%%")
    val submissionResult:EitherT[F,String,SpSuccess] =
      for{
        asc <- appSubContext(myYarnRunParam.yarnClient,myYarnRunParam.yarnParam,job)
        sr <- handleSubmit[ApplicationId](myYarnRunParam.yarnClient.submitApplication(asc))
      } yield sr
    submissionResult
  }


  private def appSubContext(yarnClient: YarnClient, sparkParam: YarnParam, job:Job)
                           (implicit F: ConcurrentEffect[F]): EitherT[F,String,ApplicationSubmissionContext] = for {
    newApp <- createYarnApplication(yarnClient)
    app = ApplicationHelper.buildApp(job,sparkParam.daemonConf,sparkParam.yarnConf)
    amc = createAMContainer(sparkParam, app)
    res <- LocalResourceHelper.createResourceMap(app)
    _   = amc.setLocalResources(res.asJava)
    am = setEnvironment(amc, sparkParam.yarnConf)
    asc = createAppSubmissionContext(am,newApp,app)
  } yield asc

  def createYarnApplication(yarnClient: YarnClient)
                           (implicit F: ConcurrentEffect[F]): EitherT[F,String,YarnClientApplication] =
    {
      println(s"My YARN RUNNER yarnClient is in state: ${yarnClient.getServiceState}")
      scala.util.Try{ yarnClient.createApplication()
      } match {
        case scala.util.Success(x) =>
          println(s"%%%%%% YARN APP CREATED: ${x}")
          EitherT.rightT[F,String](x)
        case scala.util.Failure(ex) =>
          println(s"%%%%%%% YARN RUNNER YARN APP CREATION FAILED: ${ex.getMessage}")
          EitherT.leftT[F,YarnClientApplication](ex.getMessage)
//          ex.printStackTrace()
//          throw new Exception(ex.getMessage)
      }
    }

  private def createAMContainer(yarnParam: YarnParam, application: Application) = {
    // STEP 4. CREATING YARN AM CONTAINER
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])

    // STEP 5. CREATING COMMAND STRING AND SETTING WITHIN AM CONTAINER

    val commandList = CommandHelper.fromApp(yarnParam, application)
    println(s"------------------ NEW ARGS : ${commandList} -------")
    println(s"prepareAndSubmitYarnJob SETTING COMMAND")

    amContainer.setCommands(commandList.asJava)
    //    amContainer.setCommands(List(command).asJava)
    amContainer
  }

  import scala.collection.JavaConverters._
  private def setEnvironment(amContainer: ContainerLaunchContext, yarnConf: YarnConf) = {
    // STEP 6 (or 7 if prepare resource map is used) PREPARE ENV ENTRIES AND SET AM CONTAINER ENVIRONMENT
    val env = EnvHelper.build(yarnConf)
    println(s"ENV: ${env}")
    amContainer.setEnvironment(env.asJava)

    println(s"prepareAndSubmitYarnJob LAUNCH SET ENVIRONMENT: ${amContainer.getEnvironment}")
//    println(s"prepareAndSubmitYarnJob ADDING RM TOKEN")
    amContainer
  }

  def createAppSubmissionContext(amContainer: ContainerLaunchContext
                                 , yarnClientApp: YarnClientApplication
                                , application: Application) = {
    // STEP 7 (or 8 if prepare resource map is used) PREPARE CONTAINER RESOURCES
    // LIKE MEM VCORES ETC.
    // AND OTHER SETTINGS AND SET APP CONTEXT ALONG WITH AM CONTAINER

    println(s"=============  CREATE APP SUBMISSION CONTEXT PREPARING ")
    //specify resource requirements
    val resource = Records.newRecord(classOf[Resource])

    resource.setMemory(application.resources.memory)
    resource.setVirtualCores(application.resources.cores)

    //context to launch
    val appSubmissionContext = yarnClientApp.getApplicationSubmissionContext

    appSubmissionContext.setApplicationName(application.appName)
    appSubmissionContext.setAMContainerSpec(amContainer)
    appSubmissionContext.setResource(resource)
    appSubmissionContext.setQueue(application.queueName.fold("default")(_.toString))
    appSubmissionContext.setKeepContainersAcrossApplicationAttempts(false)
    appSubmissionContext.setMaxAppAttempts(1)
    appSubmissionContext.setApplicationType("YARN")

    //submit the application
    val appId = appSubmissionContext.getApplicationId
    println(s"=============  CREATE APP SUBMISSION CONTEXT submitting application id : ${appId} ")
    // may be redundant
    appSubmissionContext.setApplicationId(appId)
    appSubmissionContext
  }

}
