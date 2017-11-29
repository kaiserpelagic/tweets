package tweets

import org.specs2._
import scalaz.scalacheck.ScalazProperties._
import org.scalacheck._
import Stat._

class StatSpec extends Properties("Stat Monoid") {

  implicit val arbitraryStat = 
    Arbitrary {
      for {
        long1 <- Arbitrary.arbitrary[Long] 
        long2 <- Arbitrary.arbitrary[Long] 
        long3 <- Arbitrary.arbitrary[Long] 
        long4 <- Arbitrary.arbitrary[Long] 
        long5 <- Arbitrary.arbitrary[Long] 
        long6 <- Arbitrary.arbitrary[Long] 
        long7 <- Arbitrary.arbitrary[Long] 
        str1 <- Arbitrary.arbitrary[String] 
        str2 <- Arbitrary.arbitrary[String] 
        str3 <- Arbitrary.arbitrary[String] 
      } yield Stat(long1,long2,long3,long4,Map(str1 -> long5),Map(str2 -> long6), Map(str3 -> long7)) 
  }

  def checkAll(name: String, props: Properties): Unit = {
    for ((name2, prop) <- props.properties) yield {
      property(name + ":" + name2) = prop
    }
  }

  checkAll("Stat Monoid", monoid.laws[Stat])
}

