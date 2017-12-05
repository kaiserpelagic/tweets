package tweets

import scalaz.stream.{Process, Sink, Channel, channel, sink}
import scalaz.stream.async.mutable.{Queue, Signal}
import scalaz.stream.async._
import scalaz.concurrent.Task
import scalaz._, Scalaz._
import _root_.argonaut.Json

/*
 * Backend for processing of tweets. The frontend streaming client is decoupled from
 * background processing via a Queue, in this case a CircularBuffer. 
 *
 * The Pipeline is responsible for extracting interesting information from tweets and
 * keeping a running total via the Stat Monoid.
 * 
 * The current implementation consists of a single consumer, but concurrency can be achieved
 * by using njoin. However, given current testing, concurrency is not needed to keep up with stream velocity.
 */
object Pipeline {

  import Tweet._
  import JsonCodecs._

  def process(queue: Queue[Json], emojiData: EmojiData): Process[Task, Stat] =
    queue.dequeue
      .through(decodeTweet)
      .stripW // remove any json blobs that didn't parse as a tweet
      .through(extract(emojiData))
      .scanMonoid[Stat]
      .observe(updateStats)

  val decodeTweet: Channel[Task, Json, String \/ Tweet] =
    channel.lift(json => 
      Task.delay(\/.fromEither(json.as[Tweet].result.left.map(_._1))))

  def extract(emojiData: EmojiData): Channel[Task, Tweet, Stat] =
    channel.lift(tweet => Task.delay(Stat.fromTweet(tweet, emojiData)))

  val updateStats: Sink[Task, Stat] =
    sink.lift(stat => currentStat.set(stat))

  val currentStat: Signal[Stat] = signalOf(Stat.empty)
}
