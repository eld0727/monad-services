package go.to.monad.simple.services

import go.to.monad.domain.{News, UserInfo}
import go.to.monad.simple.repository.UserRepository

import scala.concurrent.{ExecutionContext, Future}

trait NewsService {
  def getRecentUserNews(userId: String): Future[Seq[News]]
}

class NewsServiceImpl(userRepository: UserRepository,
                      vkService: VkService,
                      twitterService: TwitterService)
                     (implicit executionContext: ExecutionContext) extends NewsService {
  override def getRecentUserNews(userId: String): Future[Seq[News]] = {
    userRepository.info(userId).flatMap {
      case None => Future.successful(Seq.empty)
      case Some(info) =>
        val vkFuture = vkService.recentUserNews(info.vkInfo)
        val twitterFuture = twitterService.recentUserTweets(info.twitterInfo)
        for {
          vkNews <- vkFuture
          twitterNews <- twitterFuture
          _ <- userRepository.storeInfo(updateInfo(info, vkNews, twitterNews))
        } yield {
          vkNews ++ twitterNews
        }
    }
  }

  private def updateInfo(info: UserInfo, vkNews: Seq[News], tweets: Seq[News]): UserInfo = {
    info.copy(
      vkInfo = info.vkInfo.copy(lastNews = vkNews.headOption.map(_.id).orElse(info.vkInfo.lastNews)),
      twitterInfo = info.twitterInfo.copy(lastTweet = tweets.headOption.map(_.id).orElse(info.twitterInfo.lastTweet))
    )
  }
}
