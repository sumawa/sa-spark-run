package com.sa.sparkrun.params

//import cats.effect.IO
//import fs2.Stream
//import pureconfig.{ConfigReader, Derivation}
//import pureconfig.module.catseffect.loadConfigF
//
//import scala.reflect.ClassTag
//import java.nio.file.Paths
//import java.nio.file.Path
//
//import pureconfig.generic.auto._
//object ConfLoad {
//
//  def loadCnf[A](p: Path, ns: String)(
//    implicit
//    reader: Derivation[ConfigReader[A]],
//    ct: ClassTag[A]
//  ) = {
//    import pureconfig.generic.auto._
//    println(s"loading cnf: ${p} == ${ns}")
//    Stream.eval(loadConfigF[IO, A](p, ns))
//  }
//}
