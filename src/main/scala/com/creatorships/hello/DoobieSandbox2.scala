package com.creatorships.hello

import cats.Monad
import cats.effect.{Async, Blocker, Bracket, ContextShift, ExitCode, IO, IOApp, Resource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.{ExecutionContexts, Get}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.config.parser
import io.circe.generic.auto._

object DoobieSandbox2 extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    transactor[IO].use { xa =>
      for {
        logger <- Slf4jLogger.create[IO]
        member <- new DoobieMemberRepository[IO](xa).findAll
        _ <- logger.info(member.toString)
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

  class DoobieMemberRepository[F[_]: Monad](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable]) {

    implicit val memberTypeGet: Get[Option[MemberType]] = Get[String].map(MemberType.of)

    def findAll: F[Members] = selectAll.to[List].map(Records(_).toMembers).transact(transactor)

    private def selectAll: Query0[Record] =
      sql"""SELECT id, name, member_type FROM member""".query[Record]
  }

  case class Records(toSeq: Seq[Record]) extends AnyVal {

    // ここらへんで必要ならエラーログとか出せばいいと思う。Option じゃなくて、Either とか Validated とかを受け取るようにして。
    def toMembers: Members = Members(toSeq.flatMap(_.toMember))

  }

  case class Record(id: Int, name: String, maybeMemberType: Option[MemberType]) {

    def toMember: Option[Member] = maybeMemberType.map(memberType => Member(id, name, memberType))

  }

  case class Members(toSeq: Seq[Member]) extends AnyVal

  case class Member(id: Int, name: String, memberType: MemberType)

  sealed abstract class MemberType(val value: String)

  object MemberType {

    case object Admin extends MemberType("admin")

    case object General extends MemberType("general")

    private val map: Map[String, MemberType] = Seq(Admin, General).map(t => t.value -> t).toMap

    def of(value: String): Option[MemberType] = map.get(value)

  }

}
