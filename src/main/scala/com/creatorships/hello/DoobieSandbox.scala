package com.creatorships.hello

import cats.Monad
import cats.effect.{Async, Blocker, Bracket, ContextShift, ExitCode, IO, IOApp, Resource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.log.LogHandler
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.config.parser
import io.circe.generic.auto._

object DoobieSandbox extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    transactor[IO].use { xa =>
      for {
        logger <- Slf4jLogger.create[IO]
        prefactures <- new DoobiePrefectureRepository[IO](xa).findAll
        _ <- logger.info(prefactures.toString)
      } yield ExitCode.Success

    }

  def transactor[F[_]: Async: ContextShift]: Resource[F, HikariTransactor[F]] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, Database]("database"))
      connectEC <- ExecutionContexts.fixedThreadPool[F](10)
      transactEC <- ExecutionContexts.cachedThreadPool[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        config.driver,
        config.url,
        config.user,
        config.pass,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
    } yield transactor

  case class Database(driver: String, url: String, user: String, pass: String)

  class DoobiePrefectureRepository[F[_]: Monad](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable]) {
    def findAll: F[Prefectures] = selectAll.to[List].map(Prefectures).transact(transactor)

    private def selectAll: Query0[Prefecture] = sql"""SELECT code, name FROM prefecture""".queryWithLogHandler[Prefecture](LogHandler.jdkLogHandler)
  }

  case class Code(toInt: Int) extends AnyVal {

    def isMatched(id: Int): Boolean = id == toInt

  }

  case class Name(override val toString: String) extends AnyVal

  case class Prefecture(code: Code, name: Name)

  case class Prefectures(toList: List[Prefecture]) extends AnyVal {

    def find(id: Int): Option[Prefecture] = toList.find(_.code.isMatched(id))

  }

}
