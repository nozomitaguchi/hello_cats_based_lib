package com.creatorships.hello

import cats.effect.{Sync, _}
import cats.implicits._
import io.chrisdavenport.log4cats.{Logger, _}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

// 公式サンプルそのもの
object Log4catsSandbox extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- doSomething[IO]()
      _ <- safelyDoThings[IO]
      _ <- passForEasierUse[IO]
    } yield ExitCode.Success

  // Impure But What 90% of Folks I know do with log4s
  implicit def unsafeLogger[F[_]: Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  // Arbitrary Local Function Declaration
  def doSomething[F[_]: Sync](): F[Unit] =
    Logger[F].info("Logging Start Something") *>
      Sync[F].delay(println("I could be doing anything")).attempt.flatMap {
        case Left(e) => Logger[F].error(e)("Something Went Wrong")
        case Right(_) => Sync[F].pure(())
      }

  def safelyDoThings[F[_]: Sync]: F[Unit] =
    for {
      logger <- Slf4jLogger.create[F]
      _ <- logger.info("Logging at start of safelyDoThings")
      something <- Sync[F]
        .delay(println("I could do anything"))
        .onError { case e => logger.error(e)("Something Went Wrong in safelyDoThings") }
      _ <- logger.info("Logging at end of safelyDoThings")
    } yield something

  def passForEasierUse[F[_]: Sync]: F[Unit] =
    for {
      _ <- Logger[F].info("Logging at start of passForEasierUse")
      something <- Sync[F]
        .delay(println("I could do anything"))
        .onError { case e => Logger[F].error(e)("Something Went Wrong in passForEasierUse") }
      _ <- Logger[F].info("Logging at end of passForEasierUse")
    } yield something

}
