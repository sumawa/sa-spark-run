package com.sa.sparkrun.db

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{Async, Blocker, ContextShift, Sync}
import com.sa.sparkrun.conf.DatabaseConfig
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.Transactor
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext

trait TransactorFactory[A] {

  def apply[F[_]: Async: ContextShift](
                                        config: DatabaseConfig
                                      ): F[Transactor.Aux[F, A]]
}

object PooledTransactor extends TransactorFactory[HikariDataSource] {
  import cats.syntax.functor._

  override def apply[F[_]: Async: ContextShift](
                                                 conf: DatabaseConfig
                                               ): F[Transactor.Aux[F, HikariDataSource]] = {
    val connectEC       = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(conf.poolSize))
    // use a separate Thread pool for transact blocker
    val transactEC      = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
    val blocker = Blocker.liftExecutionContext(transactEC)
    makeConf(conf).map { cnf =>
      HikariTransactor.apply[F](new HikariDataSource(cnf), connectEC, blocker)
    }
  }

  // since HikariConfig is mutable let's
  // capture these mutations in a Sync effect
  private def makeConf[F[_]](
                              config: DatabaseConfig
                            )(implicit F: Sync[F]): F[HikariConfig] =
    F.delay {
      val conf = new HikariConfig
      conf.setDriverClassName(config.driver)
      conf.setJdbcUrl(config.jdbcUrl)
      conf.setUsername(config.user)
      conf.setPassword(config.pass)
      config.config.foreach {
        case (key, value) => conf.addDataSourceProperty(key, value)
      }
      conf
    }
}
