package tweets

import scala.io.Source
import _root_.argonaut._, Argonaut._
import scalaz._, Scalaz._


/*
 * Loads an emoji data file, parses it, and loads the data into a Map[Unicode, ShortName]
 */
object Emoji {
  
  import JsonCodecs._
  
  def fromPath(path: String): String \/ EmojiData = {
    for {
      raw    <- loadFile(path)  
      parsed <- \/.fromEither(Parse.parse(raw))
      json   <- \/.fromEither(parsed.as[List[(Unicode, ShortName)]].result.left.map(_._1))
    } yield json.toMap
  }

  private def loadFile(path: String): String \/ String =
    \/.fromTryCatchNonFatal(
      Source.fromInputStream(
        getClass.getResourceAsStream(path)).mkString).leftMap(_.toString)

  // Very simple test for emojis.
  def extract(tweet: Tweet, emojiData: EmojiData): List[String] =
    tweet.text.toList.flatMap(e => emojiData.get(e.toHexString.toUpperCase))
}
