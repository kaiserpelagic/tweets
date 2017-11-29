package tweets

import java.util.concurrent.{ExecutorService, ThreadFactory, Executors}
import scalaz.concurrent.Strategy

object Pools {

  def daemonThreads(name: String) = new ThreadFactory {
    def newThread(r: Runnable) = {
      val t = Executors.defaultThreadFactory.newThread(r)
      t.setDaemon(true)
      t.setName(name)
      t
    }
  }

  val defaultPool: ExecutorService =
    Executors.newFixedThreadPool(scala.math.max(8, Runtime.getRuntime.availableProcessors),
      daemonThreads("tweets-thread"))

  val defaultExecutor: Strategy =
    Strategy.Executor(defaultPool)
}
