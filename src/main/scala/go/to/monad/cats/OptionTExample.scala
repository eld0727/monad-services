package go.to.monad.cats

import cats.data.OptionT
import cats.instances.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object OptionTExample {
  def foo: Future[Option[String]] = Future.successful(Some("str"))
  def bar(str: String): Future[Option[String]] = Future.successful(Some(str + "1"))

  def fun: Future[Option[String]] = foo.flatMap {
    case None => Future.successful(None)
    case Some(str) => bar(str)
  }

  def fooT: OptionT[Future, String] = OptionT.some[Future]("str")
  def barT(str: String): OptionT[Future, String] = OptionT.some[Future](str + "1")

  def funT: OptionT[Future, String] = fooT.flatMap(barT)
}
