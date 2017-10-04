package go.to.monad.cats.services

import cats.Monad
import cats.syntax.all._
import go.to.monad.cats.repository.UserRepository
import go.to.monad.domain.{News, UserInfo}

import scala.language.higherKinds

trait NewsService[F[_]] {
  def getRecentUserNews(userId: String): F[Seq[News]]
}

class NewsServiceImpl[F[_]](implicit
                            F: Monad[F],
                            applicationContext: ApplicationContext[F],
                            userRepository: UserRepository[F],
                            vkService: VkService[F],
                            twitterService: TwitterService[F]) extends NewsService[F] {
  override def getRecentUserNews(userId: String): F[Seq[News]] = {
    userRepository.info(userId)
      .semiflatMap { info =>
        (vkService.recentUserNews(info.vkInfo), twitterService.recentUserTweets(info.twitterInfo)).tupled.flatMap {
          case (news, tweets) =>
            for {
              _ <- userRepository.storeInfo(updateInfo(info, news, tweets))
              _ <- applicationContext.put(s"${news.size} new VK news and ${tweets.size} tweets")
            } yield {
              news ++ tweets
            }
        }
      }
      .getOrElse(Seq.empty)
  }

  /*
  override def getRecentUserNews(userId: String): F[Seq[News]] = {
    val newsOptT = for {
      info <- userRepository.info(userId)
      (news, tweets) <- OptionT.liftF((
        vkService.recentUserNews(info.vkInfo),
        twitterService.recentUserTweets(info.twitterInfo)
      ).tupled)
      _ <- OptionT.liftF(userRepository.storeInfo(updateInfo(info, news, tweets)))
      _ <- OptionT.liftF(applicationContext.put(s"${news.size} new VK news and ${tweets.size} tweets"))
    } yield news ++ tweets

    newsOptT.getOrElse(Seq.empty)
  }
  */

  private def updateInfo(info: UserInfo, vkNews: Seq[News], tweets: Seq[News]): UserInfo = {
    info.copy(
      vkInfo = info.vkInfo.copy(lastNews = vkNews.headOption.map(_.id).orElse(info.vkInfo.lastNews)),
      twitterInfo = info.twitterInfo.copy(lastTweet = tweets.headOption.map(_.id).orElse(info.twitterInfo.lastTweet))
    )
  }
}
