package tweets

import org.specs2._
import scalaz.scalacheck.ScalazProperties._
import org.scalacheck._
import Stat._

class StatSpec extends Properties("Stat Monoid") {

  val threshold = 10

  implicit val arbitraryStat = 
    Arbitrary {
      for {
        long1 <- Gen.posNum[Long]
        long2 <- Gen.posNum[Long]
        long3 <- Gen.posNum[Long]
        long4 <- Gen.posNum[Long]
        str1 <- Arbitrary.arbitrary[String]
        str2 <- Arbitrary.arbitrary[String]
        str3 <- Arbitrary.arbitrary[String]
      } yield Stat(long1,long2,long3,long4, Some(Counter(str1, threshold)), Some(Counter(str2, threshold)), Some(Counter(str3, threshold)))
  }

  def checkAll(name: String, props: Properties): Unit = {
    for ((name2, prop) <- props.properties) yield {
      property(name + ":" + name2) = prop
    }
  }

  checkAll("Stat Monoid", monoid.laws[Stat])
}

