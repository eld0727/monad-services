package go.to.monad.cats.services

import cats.Monad
import cats.syntax.all._
import go.to.monad.cats.repository.VkRepository
import go.to.monad.domain.{News, VkInfo}

import scala.language.higherKinds

trait VkService[F[_]] {
  def recentUserNews(info: VkInfo): F[Seq[News]]
}

class VkServiceImpl[F[_]](implicit
                          F: Monad[F],
                          vkRepository: VkRepository[F]) extends VkService[F] {
  private val pageSize: Int = 100

  override def recentUserNews(info: VkInfo): F[Seq[News]] = loadPages(info, 0)

  private def loadPages(info: VkInfo, offset: Int): F[Seq[News]] = {
    vkRepository.wall(info.userId, offset, pageSize, Some(info.accessToken)).flatMap {
      case seq if seq.isEmpty => F.pure(seq)
      case seq =>
        info.lastNews match {
          case Some(id) if seq.exists(_.id == id) =>
            F.pure(seq.takeWhile(_.id != id))
          case _ =>
            loadPages(info, offset + pageSize).map(seq ++ _)
        }
    }
  }

}
