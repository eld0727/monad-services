package go.to.monad.simple

import go.to.monad.simple.repository.{TwitterRestRepository, UserDatabaseRepository, VkRestRepository}
import go.to.monad.simple.services.{NewsServiceImpl, TwitterServiceImpl, VkServiceImpl}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object SimpleModule {
  lazy val vkRepository = new VkRestRepository
  lazy val twitterRepository = new TwitterRestRepository
  lazy val userRepository = new UserDatabaseRepository

  lazy val vkService = new VkServiceImpl(vkRepository)
  lazy val twitterService = new TwitterServiceImpl(twitterRepository)
  lazy val newsService = new NewsServiceImpl(userRepository, vkService, twitterService)
}
