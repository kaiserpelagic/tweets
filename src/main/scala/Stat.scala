package tweets

import scalaz._, Scalaz._

/*
 * All the stats we care about, in one place. The monoid instance is used
 * in the backend processing pipeline to scan over the stream and combine
 * all the stats into one.
 */
final case class Stat(
  tweetCount: Long,
  emojiCount: Long,
  urlCount: Long,
  photoCount: Long,
  hashtags: Option[Counter[String]],
  domains: Option[Counter[String]],
  emojis: Option[Counter[String]]
)

object Stat {

  val empty: Stat = Stat(0,0,0,0, None, None, None)

  implicit val StatMonoid: Monoid[Stat] = new Monoid[Stat] {

    def zero: Stat = empty
  
    def append(s1: Stat, s2: => Stat): Stat =
      Stat(
        tweetCount = s1.tweetCount + s2.tweetCount,
        emojiCount = s1.emojiCount + s2.emojiCount,
        urlCount   = s1.urlCount   + s2.urlCount,
        photoCount = s1.photoCount + s2.photoCount,
        hashtags   = s1.hashtags  |+| s2.hashtags,
        domains    = s1.domains   |+| s2.domains,
        emojis     = s1.emojis    |+| s2.emojis
      )
  }

  implicit object StatEqual extends Equal[Stat] {
    def equal(s1: Stat, s2: Stat): Boolean = s1 == s2
  }

  def fromTweet(tweet: Tweet, emojiData: EmojiData): Stat = {

    def containsPhoto(domains: List[String]): Boolean =
        domains.exists(d => d.contains("pic.twitter.com") || d.contains("instagram.com"))
    
    val tweetCount = 1 // a tweet is always tweet
    val emojis     = Emoji.extract(tweet, emojiData)
    val domains    = Tweet.domains(tweet)
    val emojiCount = if (emojis.nonEmpty) 1 else 0
    val urlCount   = if (tweet.urls.nonEmpty) 1 else 0
    val photoCount = if (containsPhoto(domains)) 1 else 0
    val hashtagsMap = tweet.hashtags.map(t => Counter(t.text, 1000)).reduceOption(_ |+| _)
    val domainsMap  = domains.map(d => Counter(d, 1000)).reduceOption(_ |+| _)
    val emojisMap   = emojis.map(s => Counter(s, 1000)).reduceOption(_ |+| _)

    Stat(tweetCount, emojiCount, urlCount, photoCount, hashtagsMap, domainsMap, emojisMap)
  }
}
