package com.sa.sparkrun.conf

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Sync}
import pureconfig.{ConfigReader, ConfigSource, Derivation}

import scala.reflect.ClassTag
import java.nio.file.Path

import fs2.Stream
import pureconfig.module.catseffect.loadConfigF

object ConfHelper {

  import pureconfig.module.catseffect.loadF
  def loadCnfF[F[_]: ConcurrentEffect: ContextShift,A](p: Path, ns: String, blocker: Blocker)(
    implicit
    reader: Derivation[ConfigReader[A]]
    , ct: ClassTag[A]
  ): Stream[F,A] = {
    val cs = ConfigSource.default(ConfigSource.file(p)).at(ns)
    Stream.eval(loadF[F,A](cs,blocker))
  }

  def loadCnfDefault[F[_]: ConcurrentEffect: ContextShift,A](ns: String, blocker: Blocker)(
    implicit
    reader: Derivation[ConfigReader[A]]
    , ct: ClassTag[A]
  ): Stream[F,A] = {
    val cs = ConfigSource.default.at(ns)
    Stream.eval(loadF[F,A](cs,blocker))
  }

}
