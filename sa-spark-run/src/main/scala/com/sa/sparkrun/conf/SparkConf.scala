//package com.sa.sparkrun.conf

//case class EnvVar(SP: Option[String] = Some("1"), SPARK_USER: Option[String] = Some(""))
//
//case class SparkConf(action: String,
//                     appResource: String,
//                     mainClass: String,
//                     sparkProperties: Map[String, String],
//                     clientSparkVersion: String = "2.4.0",
//                     appArgs: List[String] ,
//                     environmentVariables: EnvVar)
//
//object SparkConf {
//  val namespace: String = "sparkConf"
//}


import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

//case class SubmitterConf(
//                          javaPath: String,
//                          javaHome: String,
//                          sparkHome: String,
//                          maxAttempts: Int,
//                          defaultQueue: String,
//                          connectionTimeout: Int,
//                          classpath: String,
//                          sparkJars: String
//                        )
//
//object SubmitterConf {
//  val namespace: String = "submitter"
//}

//curl -X POST http://192.168.1.1:6066/v1/submissions/create --header "Content-Type:application/json;charset=UTF-8" --data '{
//"appResource": "/home/hduser/sparkbatchapp.jar",
//"sparkProperties": {
//"spark.executor.memory": "8g",
//"spark.master": "spark://192.168.1.1:7077",
//"spark.driver.memory": "8g",
//"spark.driver.cores": "2",
//"spark.eventLog.enabled": "false",
//"spark.app.name": "Spark REST API - PI",
//"spark.submit.deployMode": "cluster",
//"spark.jars": "/home/user/spark-examples_versionxx.jar",
//"spark.driver.supervise": "true"
//},
//"clientSparkVersion": "2.4.0",
//"mainClass": "org.apache.spark.examples.SparkPi",
//"environmentVariables": {
//"SPARK_ENV_LOADED": "1"
//},
//"action": "CreateSubmissionRequest",
//"appArgs": [
//"80"
//]
//}'

//case class SparkConf()

//  case class Person(name: String, age: Option[Int], phoneNumbers: List[String])
//
//  object Person {
//    implicit val personDecoder: Decoder[Person] = new Decoder[Person] {
//      override def apply(c: HCursor): Decoder.Result[Person] =
//        for {
//          name <- c.get[String]("name")
//          age <- c.get[Option[Int]]("age")
//          phoneNumbers <- c.get[List[String]]("phone")
//        } yield Person(name, age, phoneNumbers)
//    }
//
//    implicit val personEncoder: Encoder[Person] = new Encoder[Person] {
//      override def apply(a: Person): Json = Json.obj(
//        "name" -> a.name.asJson,
//        "age" -> a.age.asJson,
//        "phone" -> a.phoneNumbers.asJson
//      )
//    }
//
//    implicit val personEntityDecoder: EntityDecoder[IO, Person] = jsonOf[IO, Person]
//    implicit val personEntityEncoder: EntityEncoder[IO, Person] = jsonEncoderOf[IO, Person]
//
//  }
