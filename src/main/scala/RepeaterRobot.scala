package io.magica.bots.repeater

import com.roundeights.hasher.Implicits._
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.methods.{ForwardMessage, SendMessage}
import info.mukel.telegrambot4s.models._
import redis.RedisClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class RepeaterRobot(val token: String) extends TelegramBot with Polling with Commands {
  private val MESSAGE_TIMEOUT = 60 // Clear message after 1min.
  private val REPEAT_AFTER = 3 // After 3 same text.

  private val redis = RedisClient(host = "localhost", port = 6379, db = 0)

  override def receiveMessage(msg: Message): Unit = {
    msg.text match {
      case Some(text) =>
        val key = s"chat:${msg.chat.id}:${text.sha256}"
        val result = Await.result(redis.incr(key), 1 seconds)
        redis.expire(key, MESSAGE_TIMEOUT)
        if (result == REPEAT_AFTER) {
          msg.forwardFrom match {
            case Some(_) =>
              request(ForwardMessage(msg.source, msg.source, false, msg.messageId))
            case None =>
              request(SendMessage(msg.source, text))
          }
        }
      case None => // Ignore empty text.
    }
  }
}

object RepeaterRobot {
  def main(args: Array[String]): Unit = {
    new RepeaterRobot(Source.fromFile("token").getLines().mkString).run()
  }
}
