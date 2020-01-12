package com.creatorships.hello

import com.creatorships.hello.CirceSandbox2.Database.{Driver, Pass, Url, User}
import io.circe
import io.circe.Decoder
import io.circe.config.parser
import io.circe.generic.auto._
import shapeless._

object CirceSandbox2 {

//  implicit def encoderValueClass[T <: AnyVal, V](
//    implicit
//    g: Lazy[Generic.Aux[T, V :: HNil]],
//    e: Encoder[V]
//  ): Encoder[T] = Encoder.instance { value =>
//    e(g.value.to(value).head)
//  }

  implicit def decoderValueClass[T <: AnyVal, V](
    implicit
    g: Lazy[Generic.Aux[T, V :: HNil]],
    d: Decoder[V]
  ): Decoder[T] = Decoder.instance { cursor =>
    d(cursor).map { value =>
      g.value.from(value :: HNil)
    }
  }

  case class Database(driver: Driver, url: Url, user: User, pass: Pass)

  object Database {
    case class Driver(override val toString: String) extends AnyVal
    case class Url(override val toString: String) extends AnyVal
    case class User(override val toString: String) extends AnyVal
    case class Pass(override val toString: String) extends AnyVal
  }

  def main(args: Array[String]): Unit = {
    val database: Either[circe.Error, Database] = parser.decodePath[Database]("database")
    println(database)
  }

}
