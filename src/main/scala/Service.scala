package tweets

import org.http4s._
import org.http4s.dsl._
import org.http4s.argonaut._
import scalaz.concurrent.Task
import scala.concurrent.duration._
import java.time.Instant
import scalaz.stream.async.mutable.{Signal,Queue}

/*
 * Http service which provides the current statistics collected from twitter
 */
class Service(start: Instant, stats: Signal[Stat]) {

  import JsonCodecs._

  val service: HttpService = HttpService {

    case GET -> Root / "stats" =>
      stats.get.flatMap { stat =>
        val dur = duration(start, now)
        val percentEmoji = formatDouble(100 * stat.emojiCount.toDouble / stat.tweetCount.toDouble)
        val percentUrl = formatDouble(100 * stat.urlCount.toDouble / stat.tweetCount.toDouble)
        val percentPhoto = formatDouble(100 * stat.photoCount.toDouble / stat.tweetCount.toDouble)
        val topHash = stat.hashtags.get.topK(5).map(x => Count(x._1, x._2))
        val topDomain = stat.domains.get.topK(5).map(x => Count(x._1, x._2))
        val topEmoji = stat.emojis.get.topK(5).map(x => Count(x._1, x._2))
        val avg = Average(stat.tweetCount, dur)
        val res = TwitterReport(stat.tweetCount, stat.emojiCount, stat.photoCount, stat.urlCount,
                    formatDouble(avg.perSecond), formatDouble(avg.perMinute), formatDouble(avg.perHour),
                    percentEmoji, percentUrl, percentPhoto, topHash, topEmoji, topDomain) 
        Ok(res)
      }
  }

  private def now = Instant.now

  private def duration(start: Instant, end: Instant): FiniteDuration = 
    FiniteDuration(end.toEpochMilli - start.toEpochMilli, MILLISECONDS)

  private def formatDouble(d: Double): Double =
    BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
}
