package com.creatorships.hello

import io.circe
import io.circe.generic.auto._
import io.circe.config.parser

object CirceSandbox {

  case class Database(driver: String, url: String, user: String, pass: String)

  def main(args: Array[String]): Unit = {
    val database: Either[circe.Error, Database] = parser.decodePath[Database]("database")
    println(database)
  }

}
