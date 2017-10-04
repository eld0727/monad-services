package go.to.monad.cats.repository

import cats._
import go.to.monad.domain.News

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.util.Random

trait VkRepository[F[_]] {
  def wall(ownerId: String, offset: Int, size: Int, accessToken: Option[String]): F[Seq[News]]
}

object VkRepository {
  def apply[F[_]](implicit vkRepository: VkRepository[F]): VkRepository[F] = vkRepository

  def apply[F[_], G[_]](repository: VkRepository[F])
                       (implicit
                        G: Monad[G],
                        FG: F ~> G): VkRepository[G] = GVkRepository[F, G](G, FG, repository)

  implicit def GVkRepository[F[_], G[_]](implicit
                                         G: Monad[G],
                                         FG: F ~> G,
                                         repository: VkRepository[F]): VkRepository[G] = {
    new VkRepository[G] with RepositoryK[F, G] {
      override def wall(ownerId: String, offset: Int, size: Int, accessToken: Option[String]): G[Seq[News]] = {
        convert(repository.wall(ownerId, offset, size, accessToken))
      }
    }
  }
}

class VkRestRepository(implicit executionContext: ExecutionContext) extends VkRepository[Future] {
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