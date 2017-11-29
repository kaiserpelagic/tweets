package tweets

import _root_.argonaut._, Argonaut._
import org.http4s.argonaut._
import org.http4s._

/*
 * Json codecs for the entire project
 */
object JsonCodecs {

  implicit val TweetHashDecode: DecodeJson[TweetHashtag] =
    DecodeJson[TweetHashtag](c =>
      (c --\ "text").as[String].map(TweetHashtag(_))
    )

  implicit val TweetUrlDecode: DecodeJson[TweetUrl] =
    DecodeJson[TweetUrl](c =>
      (c --\ "expanded_url").as[String].map(TweetUrl(_))
    )

  implicit val TweetDecode: DecodeJson[Tweet] = 
    DecodeJson[Tweet](c => for {
      tweet <- (c --\ "text").as[String]
      hash  <- (c --\ "entities" --\ "hashtags").as[List[TweetHashtag]]
      urls  <- (c --\ "entities" --\ "urls").as[List[TweetUrl]]
    } yield Tweet(tweet, hash, urls))

  implicit val AverageEncoder: EncodeJson[Average] =
    EncodeJson[Average]((tf: Average) =>
      ("per_second" := tf.perSecond) ->:
      ("per_minute" := tf.perMinute) ->:
      ("per_hour"   := tf.perHour) ->:
      jEmptyObject
    )

  // use short_name instead of name because name is optionall and short_name is not
  implicit val emojiDecoder: DecodeJson[(Unicode, ShortName)] =
    DecodeJson(c =>
      for {
        name <- (c --\ "short_name").as[ShortName] 
        unified <- (c --\ "unified").as[Unicode]
      } yield (unified, name)
    )

  implicit val countEncoder: EncodeJson[Count] =
    EncodeJson[Count]((oc: Count) =>
      ("name"  := oc.name) ->:
      ("count" := oc.count) ->:
      jEmptyObject
    )

  implicit val statEncoder: EncodeJson[TwitterReport] =
    EncodeJson[TwitterReport]((tr: TwitterReport) =>
      ("tweets_total"      := tr.totalTweets) ->:
      ("emojis_total"      := tr.totalEmoji) ->:
      ("photos_total"      := tr.totalPhoto) ->:
      ("urls_total"        := tr.totalUrl) ->:
      ("tweets_per_second" := tr.perSecond) ->:
      ("tweets_per_minute" := tr.perMinute) ->:
      ("tweets_per_hour"   := tr.perHour) ->:
      ("emoji_percent"     := tr.percentEmoji) ->:
      ("url_percent"       := tr.percentUrl) ->:
      ("photo_percent"     := tr.percentPhoto) ->:
      ("top_hashtags"      := tr.topHashtags) ->:
      ("top_emojis"        := tr.topEmojis) ->:
      ("top_domains"       := tr.topDomains) ->:
      jEmptyObject
    )

  implicit def tweetFreqEntityEncoder[A: EncodeJson]: EntityEncoder[A] =
    jsonEncoderOf[A]
}
