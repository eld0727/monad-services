package go.to.monad.cats

import cats.instances.future._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.language.higherKinds


object CatsApp {
  import CatsModule._

  def main(args: Array[String]): Unit = {
    println("some user:")
    val (ctx, res) = Await.result(newsService.getRecentUserNews("some user").run(List.empty), Duration.Inf)
    println(s"ctx: $ctx")
    println(res.mkString("\n"))

    println("other user:")
    val (ctx2, res2) = Await.result(newsService.getRecentUserNews("other user").run(List.empty), Duration.Inf)
    println(s"ctx: $ctx2")
    println(res2.mkString("\n"))
  }
}
