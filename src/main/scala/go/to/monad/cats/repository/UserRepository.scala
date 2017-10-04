package go.to.monad.cats.repository

import cats._
import cats.data.OptionT
import go.to.monad.domain.{TwitterInfo, UserInfo, VkInfo}

import scala.language.higherKinds
import scala.util.Random

trait UserRepository[F[_]] {
  def info(userId: String): OptionT[F, UserInfo]

  def storeInfo(userInfo: UserInfo): F[Unit]
}

object UserRepository {
  def apply[F[_]](implicit userRepository: UserRepository[F]): UserRepository[F] = userRepository

  def apply[F[_], G[_]](repository: UserRepository[F])
                       (implicit
                        G: Monad[G],
                        FG: F ~> G): UserRepository[G] = GUserRepository[F, G](G, FG, repository)

  implicit def GUserRepository[F[_], G[_]](implicit
                                           G: Monad[G],
                                           FG: F ~> G,
                                           repository: UserRepository[F]): UserRepository[G] = {
    new UserRepository[G] with RepositoryK[F, G] {
      override def info(userId: String): OptionT[G, UserInfo] = {
        convertOptT(repository.info(userId))
      }

      override def storeInfo(userInfo: UserInfo): G[Unit] = {
        convert(repository.storeInfo(userInfo))
      }
    }

  }
}

class UserInMemoryRepository extends UserRepository[Id] {

  override def info(userId: String): OptionT[Id, UserInfo] = OptionT[Id, UserInfo] {
    Thread.sleep(Random.nextInt(100) + 50)
    println("user info")
    if (userId == "some user") {
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

  override def storeInfo(userInfo: UserInfo): Id[Unit] = {
    println(s"store $userInfo")
  }
}
