package go.to.monad.simple.services

import go.to.monad.domain.{News, VkInfo}
import go.to.monad.simple.repository.VkRepository

import scala.concurrent.{ExecutionContext, Future}

trait VkService {
  def recentUserNews(info: VkInfo): Future[Seq[News]]
}

class VkServiceImpl(vkRepository: VkRepository)
                   (implicit executionContext: ExecutionContext) extends VkService {
  private val pageSize: Int = 100

  override def recentUserNews(info: VkInfo): Future[Seq[News]] = loadPages(info, 0)

  private def loadPages(info: VkInfo, offset: Int): Future[Seq[News]] = {
    vkRepository.wall(info.userId, offset, pageSize, Some(info.accessToken)).flatMap {
      case seq if seq.isEmpty => Future.successful(seq)
      case seq =>
        info.lastNews match {
          case Some(id) if seq.exists(_.id == id) =>
            Future.successful(seq.takeWhile(_.id != id))
          case _ =>
            loadPages(info, offset + pageSize).map(seq ++ _)
        }
    }
  }
}
