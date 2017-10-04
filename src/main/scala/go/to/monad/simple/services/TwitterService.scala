package go.to.monad.simple.services

import go.to.monad.domain.{News, TwitterInfo}
import go.to.monad.simple.repository.TwitterRepository

import scala.concurrent.{ExecutionContext, Future}

trait TwitterService {
  def recentUserTweets(info: TwitterInfo): Future[Seq[News]]
}

class TwitterServiceImpl(twitterRepository: TwitterRepository)
                        (implicit executionContext: ExecutionContext) extends TwitterService {

  private val pageSize = 100

  override def recentUserTweets(info: TwitterInfo): Future[Seq[News]] = loadTweets(info, since = None)

  private def loadTweets(info: TwitterInfo, since: Option[Long]): Future[Seq[News]] = {
    twitterRepository.tweets(info.userId, info.accessToken, pageSize, since, info.lastTweet).flatMap {
      case seq if seq.isEmpty => Future.successful(seq)
      case seq =>
        val last = seq.last
        info.lastTweet match {
          case Some(id) if last.id == id => Future.successful(seq)
          case _ => loadTweets(info, Some(last.id)).map(seq ++ _)
        }
    }
  }
}
