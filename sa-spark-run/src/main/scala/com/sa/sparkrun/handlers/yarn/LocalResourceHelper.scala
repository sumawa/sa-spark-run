package com.sa.sparkrun.handlers.yarn

import cats.effect.ConcurrentEffect
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.records.{LocalResource, LocalResourceType, LocalResourceVisibility}
import org.apache.hadoop.yarn.util.{ConverterUtils, Records}

object LocalResourceHelper{

  import cats.syntax.functor._
  import cats.syntax.flatMap._

  val hadoopCnf: org.apache.hadoop.conf.Configuration = {
    val conf = new Configuration
    conf.set("fs.default.name","hdfs://127.0.0.1:9000")
    conf
  }
  import cats.data.EitherT
  import cats.implicits._

  def createResourceMap[F[_]](app: Application)
                             (implicit F: ConcurrentEffect[F]): EitherT[F,String,Map[String,LocalResource]] ={
    scala.util.Try(FileSystem.get(hadoopCnf).getFileStatus(new Path(app.jarPath))) match {
      case scala.util.Success(jarStat) => EitherT.rightT[F,String] {
        val resource = Records.newRecord(classOf[LocalResource])
        resource.setResource(ConverterUtils.getYarnUrlFromPath(new Path(app.jarPath)))
        resource.setSize(jarStat.getLen)
        resource.setTimestamp(jarStat.getModificationTime)
        resource.setType(LocalResourceType.FILE)
        resource.setVisibility(LocalResourceVisibility.APPLICATION)
        Map("__app__.jar" -> resource)
      }
      case scala.util.Failure(ex) =>
        println(s"%%%% FAILURE HAPPENED : ${ex.getMessage}")
        EitherT.leftT[F,Map[String,LocalResource]] (ex.getMessage)

    }}
//    fromPath(new Path(app.jarPath)).fold( msg => EitherT.left[Map[String,LocalResource]](F.delay(msg))
//      , localResourceMap => )


//    fromPath(new Path(app.jarPath))
//      .map { res =>
//        Map(
//          "__app__.jar" -> res
//        )
//      }

  import cats.syntax.functor._
  import cats.syntax.flatMap._

//  def fromPath[F[_]](path: Path)(implicit F: ConcurrentEffect[F]): EitherT[F,String,LocalResource] =
//    for {
////      _  <- F.delay(println(s"creating local resource from ${path.toString}"))
//      lr <- createFromPath(path)
//    } yield lr
//
//  private def createFromPath[F[_]](path: Path)(implicit F: ConcurrentEffect[F]): EitherT[F,String,LocalResource] =
//    scala.util.Try(FileSystem.get(hadoopCnf).getFileStatus(path)) match {
//      case scala.util.Success(jarStat) => EitherT.rightT[F,String] {
//        val resource = Records.newRecord(classOf[LocalResource])
//        resource.setResource(ConverterUtils.getYarnUrlFromPath(path))
//        resource.setSize(jarStat.getLen)
//        resource.setTimestamp(jarStat.getModificationTime)
//        resource.setType(LocalResourceType.FILE)
//        resource.setVisibility(LocalResourceVisibility.APPLICATION)
//        resource
//      }
//      case scala.util.Failure(ex) => EitherT.leftT[F,LocalResource] (ex.getMessage)
//  }

}
