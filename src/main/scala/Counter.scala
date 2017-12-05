package tweets

import scalaz._, Scalaz._

/*
 * Counter which keeps track of the top-k frequent items based on
 * the Lossy Count algorithm.
 *
 * The trade off being made here is accuracy for memory. Since we are dealing
 * with an infinite stream, storing counters for each item in an
 * unbounded set is not possible. 
 * 
 * The algorithm here works by decrementing all counters by one
 * after N items have been seen. If any counter reaches zero it is removed
 * from the set of counters.
 * 
 * Note: this is similar to Lossy Count but does not implement the entire algorithm.
 */
final case class Counter[T](freqs: Map[T, Long], threshold: Long) {
  
  def topK(k: Int): List[(T,Long)] =
    freqs.toList.sortWith(_._2 > _._2).take(k)

  private def decByOne(map: Map[T, Long]): Map[T,Long] = {
    val mmap = scala.collection.mutable.Map[T,Long]()
    map.foreach {
      case (item, freq) if freq > 1L => mmap += item -> (freq - 1L)
      case _ => () // remove from map
    }
    mmap.toMap
  }

  def ++(that: Counter[T]): Counter[T] = {
    // uses a Map[K,V] union monoid instance with the Type V append
    val combined = freqs |+| that.freqs
    val map = if (combined.size >= threshold) decByOne(combined) else combined
    Counter(map, threshold)
  }
}

object Counter {

  def apply[T](item: T, threshold: Long): Counter[T] =
    Counter(Map(item -> 1L), threshold)

  implicit def CounterSemigroup[T] = new Semigroup[Counter[T]] {
    def append(c1: Counter[T], c2: => Counter[T]): Counter[T] = c1 ++ c2 
  }
}
