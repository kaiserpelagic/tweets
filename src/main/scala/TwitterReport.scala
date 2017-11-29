package tweets

/*
 * Data wrapper containing all the statistics we are gathering
 */
final case class TwitterReport(
  totalTweets: Long,
  totalEmoji: Long,
  totalPhoto: Long,
  totalUrl: Long,
  perSecond: Double,
  perMinute: Double,
  perHour: Double,
  percentEmoji: Double,
  percentUrl: Double,
  percentPhoto: Double,
  topHashtags: List[Count],
  topEmojis: List[Count],
  topDomains: List[Count]
)

final case class Count(name: String, count: Long)
