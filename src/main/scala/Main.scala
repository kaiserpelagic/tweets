package tweets

import org.http4s.{Request, Method, Uri}
import org.http4s.client.blaze._
import scalaz.stream.async.mutable.{Signal,Queue}
import scalaz.stream.{Process}
import _root_.argonaut.Json
import scalaz.concurrent.Task
import java.util.concurrent.ExecutorService
import org.http4s.server.blaze._
import org.http4s.server.syntax._

object Main {

  def main(args: Array[String]): Unit = {

    if (args.length != 4) {
      println("usage: <consumer key> <consumer secret> <access token> <access token secret>")
    } else {
        
      val config = TwitterConfig(args(0), args(1), args(2), args(3))

      val emojiData = Emoji.fromPath("/emoji.json")
        // yolo, we should bomb out if parsing failed
        .leftMap(e => throw new RuntimeException(s"failed to parse emoji data: $e"))
        .toOption.get

      val client = PooledHttp1Client()
      val twitter = TwitterStreaming(config, client)
      val request = Request(Method.GET, Uri.uri("https://stream.twitter.com/1.1/statuses/sample.json"))
      val stream = twitter.stream(request)

      val queue: Queue[Json] = scalaz.stream.async.circularBuffer(10000)

      runBackground("backend", Pipeline.process(queue, emojiData))(Pools.defaultPool)

      runBackground("frontent", stream.map(json =>
        queue.enqueueOne(json).unsafePerformAsync(_ => Task.now(()))))(Pools.defaultPool)

      val service = new Service(java.time.Instant.now, Pipeline.currentStat)
   
      BlazeBuilder
        .bindHttp(8080, "localhost")
        .mountService(service.service, "/")
        .run
        .awaitShutdown()
    }
  }

  def runBackground[A](name: String, p: Process[Task, A])(ex: ExecutorService): Unit = {
    println(s"starting background process $name")
    Task.fork(p.run)(ex).unsafePerformAsync(_.fold(
      e => println(s"fatal error in background process: name=${name} $e"),
      _ => println(s"background process completed unexpectedly without exception: name=${name}")
    ))
  }
}

