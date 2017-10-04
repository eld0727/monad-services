package go.to.monad.simple.repository

import go.to.monad.domain.News

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait TwitterRepository {
  def tweets(userId: String, accessToken: String, count: Int, sinceTweet: Option[Long], toTweet: Option[Long]): Future[Seq[News]]
}

class TwitterRestRepository(implicit executionContext: ExecutionContext) extends TwitterRepository {
  override def tweets(userId: String, accessToken: String, count: Int, sinceTweet: Option[Long], toTweet: Option[Long]): Future[Seq[News]] = Future {
    Thread.sleep(Random.nextInt(100) + 50)
    println("twitter")
    Seq(
      News(5, "Some text from Twitter"),
      News(4, "Some text from Twitter"),
      News(3, "Some text from Twitter"),
      News(2, "Some text from Twitter"),
      News(1, "Some text from Twitter")
    )
  }
}
