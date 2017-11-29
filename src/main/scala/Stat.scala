package tweets

import scalaz._, Scalaz._
import scala.collection.immutable.Map

/*
 * All the stats we care about, in one place. Occurances of hashtags, domains, and emojis
 * are stored in a Map. These will continue to grow without bound.
 * Some kind of pruning mechanism should be implemented to make it's more robust.
 */
final case class Stat(
  tweetCount: Long,
  emojiCount: Long,
  urlCount: Long,
  photoCount: Long,
  hashtags: Map[String, Long],
  domains: Map[String, Long],
  emojis: Map[String, Long]
)

object Stat {

  val empty: Stat = Stat(0,0,0,0, Map.empty[String,Long], Map.empty[String,Long], Map.empty[String,Long])

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
    
    // Very simple test for emojis. 
    // TODO: explain how we could do better about parsing
    def extractEmojis(text: String): List[String] =
      text.toList.flatMap(e => emojiData.get(e.toHexString.toUpperCase))
    
    val tweetCount = 1 // a tweet is always tweet
    val emojis     = extractEmojis(tweet.text)
    val domains    = Tweet.domains(tweet)
    val emojiCount = if (emojis.length > 0) 1 else 0
    val urlCount   = if (tweet.urls.nonEmpty) 1 else 0
    val photoCount = if (containsPhoto(domains)) 1 else 0
    val hashtagsMap = tweet.hashtags.map(_.text -> 1L).toMap
    val domainsMap  = domains.map(_ -> 1L).toMap
    val emojisMap   = emojis.map(_ -> 1L).toMap 

    Stat(tweetCount, emojiCount, urlCount, photoCount, hashtagsMap, domainsMap, emojisMap)
  }
}
