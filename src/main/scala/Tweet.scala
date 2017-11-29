package tweets

/*
 * Simple representation of a tweet. Contains only the information
 * that we are interested in gathering statistics on.
 */
final case class TweetHashtag(text: String)

final case class TweetUrl(url: String)

final case class Tweet(text: String, hashtags: List[TweetHashtag], urls: List[TweetUrl])

object Tweet {

  /* 
   * TODO: handle errors, assumption here is that 
   * anything twitter says is a URL is parsable as one
   */
  def domains(tweet: Tweet): List[String] ={
    tweet.urls.map { url =>
      val host = new java.net.URI(url.url).getHost
      if (host.startsWith("www")) host.substring(4)
      else host
    }
  }
}
