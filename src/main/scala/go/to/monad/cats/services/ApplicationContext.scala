package go.to.monad.cats.services

trait ApplicationContext[F[_]] {
  def put(msg: String): F[Unit]
}
