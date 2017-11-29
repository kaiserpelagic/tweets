package tweets

import org.http4s._
import org.http4s.client.oauth1
import scalaz.concurrent.Task
import scalaz.stream.{Process}
import jawnstreamz._
import _root_.argonaut.Json


final case class TwitterConfig(
  consumerKey: String,
  consumerSecret: String, 
  accessToken: String,
  accessTokenSecret: String 
)

/*
 * Inspired by http4s documentation: http://http4s.org/v0.16/streaming/
 */
final case class TwitterStreaming(config: TwitterConfig, client: org.http4s.client.Client) {

  // jawnstreamz needs to know what JSON AST you want
  implicit val facade = _root_.argonaut.JawnParser.facade

  /* These values are created by a Twitter developer web app.
   * OAuth signing is an effect due to generating a nonce for each `Request`.
   */
  def sign(req: Request): Task[Request] = {
    val consumer = oauth1.Consumer(config.consumerKey, config.consumerSecret)
    val token    = oauth1.Token(config.accessToken, config.accessTokenSecret)
    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
  }

  /* Sign the incoming `Request`, stream the `Response`, and `parseJsonStream` the `Response`.
   * `sign` returns a `Task`, so we need to `Process.eval` it to use a for-comprehension.
   */
  def stream(req: Request): Process[Task, Json] =
    for {
      signed <- Process.eval(sign(req))
      result <- client.streaming(signed)(_.body.parseJsonStream)
    } yield result
}
