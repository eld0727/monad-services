package go.to.monad.simple.repository

import go.to.monad.domain.News

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait VkRepository {
  def wall(ownerId: String, offset: Int, size: Int, accessToken: Option[String]): Future[Seq[News]]
}

class VkRestRepository(implicit executionContext: ExecutionContext) extends VkRepository {
  override def wall(ownerId: String, offset: Int, size: Int, accessToken: Option[String]): Future[Seq[News]] = Future {
    Thread.sleep(Random.nextInt(100) + 50)
    println("vk")
    Seq(
      News(5, "Some text from VK"),
      News(4, "Some text from VK"),
      News(3, "Some text from VK"),
      News(2, "Some text from VK"),
      News(1, "Some text from VK")
    )
  }
}