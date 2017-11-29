package tweets

import scala.concurrent.duration._

/*
 * Average over the total number of tweets for a duration.
 * Future work, calculate a moving average to detect spikes and trends.
 */
final case class Average(total: Long, duration: FiniteDuration) {

  private val millis = duration.toMillis.toDouble
  private val average = total.toDouble / millis

  val perSecond: Double = average * Average.millisPerSecond
  val perMinute: Double = average * Average.millisPerMinute
  val perHour: Double   = average * Average.millisPerHour
}

object Average {
  val millisPerSecond = FiniteDuration(1, SECONDS).toMillis
  val millisPerMinute = FiniteDuration(1, MINUTES).toMillis
  val millisPerHour = FiniteDuration(1, HOURS).toMillis
}
