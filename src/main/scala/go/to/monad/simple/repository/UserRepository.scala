package go.to.monad.simple.repository

import go.to.monad.domain.{TwitterInfo, UserInfo, VkInfo}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait UserRepository {
  def info(userId: String): Future[Option[UserInfo]]

  def storeInfo(userInfo: UserInfo): Future[Unit]
}

class UserDatabaseRepository(implicit executionContext: ExecutionContext) extends UserRepository {
  override def info(userId: String): Future[Option[UserInfo]] = Future {
    Thread.sleep(Random.nextInt(100) + 50)
    println("user info")
    if(userId == "some user") {
      Some(
        UserInfo(
          id = "some user",
          vkInfo = VkInfo(
            userId = "vk user",
            accessToken = "vk token",
            lastNews = Some(3)
          ),
          twitterInfo = TwitterInfo(
            userId = "twitter user",
            accessToken = "twitter token",
            lastTweet = Some(1)
          )
        )
      )
    } else None
  }

  override def storeInfo(userInfo: UserInfo): Future[Unit] = {
    println(s"store $userInfo")
    Future.unit
  }
}
