package go.to.monad.cats.repository

import cats.{Monad, ~>}
import go.to.monad.domain.News

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.util.Random

trait TwitterRepository[F[_]] {
  def tweets(userId: String, accessToken: String, count: Int, sinceTweet: Option[Long], toTweet: Option[Long]): F[Seq[News]]
}

object TwitterRepository {
  def apply[F[_]](implicit twitterRepository: TwitterRepository[F]): TwitterRepository[F]= twitterRepository

  def apply[F[_], G[_]](repository: TwitterRepository[F])
                       (implicit
                        G: Monad[G],
                        FG: F ~> G): TwitterRepository[G] = GTwitterRepository[F, G](G, FG, repository)

  implicit def GTwitterRepository[F[_], G[_]](implicit
                                         G: Monad[G],
                                         FG: F ~> G,
                                         repository: TwitterRepository[F]): TwitterRepository[G] = {
    new TwitterRepository[G] with RepositoryK[F, G] {
      override def tweets(userId: String, accessToken: String, count: Int, sinceTweet: Option[Long], toTweet: Option[Long]): G[Seq[News]] = {
        convert(repository.tweets(userId, accessToken, count, sinceTweet, toTweet))
      }
    }
  }
}

class TwitterRestRepository(implicit executionContext: ExecutionContext) extends TwitterRepository[Future] {
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