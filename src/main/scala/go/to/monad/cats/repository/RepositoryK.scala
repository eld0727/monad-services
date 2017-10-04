package go.to.monad.cats.repository

import cats._
import cats.data.OptionT
import cats.syntax.all._

import scala.language.higherKinds

trait RepositoryK[F[_], G[_]] {
  def convert[A](value: => F[A])(implicit G: Monad[G], FG: F ~> G): G[A] = {
    G.pure(()).flatMap(_ => FG(value))
  }

  def convertOptT[A](opt: => OptionT[F, A])(implicit G: Monad[G], FG: F ~> G): OptionT[G, A] = OptionT {
    G.pure(()).flatMap(_ => FG(opt.value))
  }
}
