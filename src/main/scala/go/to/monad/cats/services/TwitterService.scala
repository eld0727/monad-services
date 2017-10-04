package go.to.monad.cats.services

import cats.Monad
import cats.syntax.all._
import go.to.monad.cats.repository.TwitterRepository
import go.to.monad.domain.{News, TwitterInfo}

import scala.language.higherKinds

trait TwitterService[F[_]] {
  def recentUserTweets(info: TwitterInfo): F[Seq[News]]
}

class TwitterServiceImpl[F[_]](implicit
                               F: Monad[F],
                               twitterRepository: TwitterRepository[F]) extends TwitterService[F] {

  private val pageSize = 100

  override def recentUserTweets(info: TwitterInfo): F[Seq[News]] = loadTweets(info, since = None)

  private def loadTweets(info: TwitterInfo, since: Option[Long]): F[Seq[News]] = {
    twitterRepository.tweets(info.userId, info.accessToken, pageSize, since, info.lastTweet).flatMap {
      case seq if seq.isEmpty => F.pure(seq)
      case seq =>
        val last = seq.last
        info.lastTweet match {
          case Some(id) if last.id == id => F.pure(seq)
          case _ => loadTweets(info, Some(last.id)).map(seq ++ _)
        }
    }
  }
}
