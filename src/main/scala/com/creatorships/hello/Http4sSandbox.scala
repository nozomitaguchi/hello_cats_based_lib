package com.creatorships.hello

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.creatorships.hello.DoobieSandbox.{DoobiePrefectureRepository, transactor}
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

object Http4sSandbox extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    transactor[IO].use { xa =>
      val repository = new DoobiePrefectureRepository[IO](xa)
      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(routes(repository).orNotFound)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }

  def routes(repository: DoobiePrefectureRepository[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "prefecture" / IntVar(id) =>
        val result = repository.findAll.map(
          _.find(id).map(prefecture => s"id $id = ${prefecture.name}.").getOrElse(s"not found id $id.")
        )
        Ok(result)
    }

}
