package tweets

import scala.util.Try

/*
 * Simple representation of a tweet. Contains only the information
 * that we are interested in gathering statistics on.
 */
final case class TweetHashtag(text: String)

final case class TweetUrl(url: String)

final case class Tweet(text: String, hashtags: List[TweetHashtag], urls: List[TweetUrl])

object Tweet {

  def domains(tweet: Tweet): List[String] ={
    tweet.urls.flatMap { url =>
      Try {
        val host = new java.net.URI(url.url).getHost
        if (host.startsWith("www")) host.substring(4)
        else host
      }.toOption
    }
  }
}
