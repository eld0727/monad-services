package go.to.monad.cats

import cats._
import cats.instances.all._
import cats.arrow.FunctionK
import cats.data.StateT
import go.to.monad.cats.repository._
import go.to.monad.cats.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

object CatsModule {

  type AppState[A] = StateT[Future, List[String], A]

  implicit val applicationContext: ApplicationContext[AppState] = new ApplicationContext[AppState] {
    override def put(msg: String): AppState[Unit] = StateT.modify(msg :: _)
  }

  implicit val idToAppState: Id ~> AppState = new FunctionK[Id, AppState] {
    override def apply[A](fa: Id[A]): AppState[A] = StateT.pure(fa)
  }

  implicit val futureToAppState: Future ~> AppState = new FunctionK[Future, AppState] {
    override def apply[A](fa: Future[A]): AppState[A] = StateT.lift(fa)
  }

  implicit lazy val vkRepository: VkRepository[AppState] = VkRepository(new VkRestRepository)

  implicit lazy val twitterRepository: TwitterRepository[AppState] = TwitterRepository(new TwitterRestRepository)

  implicit lazy val userRepository: UserRepository[AppState] = UserRepository(new UserInMemoryRepository)

  implicit lazy val vkService: VkService[AppState] = new VkServiceImpl[AppState]

  implicit lazy val twitterService: TwitterService[AppState] = new TwitterServiceImpl[AppState]

  implicit lazy val newsService: NewsService[AppState] = new NewsServiceImpl[AppState]
}