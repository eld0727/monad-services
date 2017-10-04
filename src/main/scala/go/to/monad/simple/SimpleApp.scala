package go.to.monad.simple



import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SimpleApp {
  import SimpleModule._

  def main(args: Array[String]): Unit = {
    println("some user:")
    println(Await.result(newsService.getRecentUserNews("some user"), Duration.Inf).mkString("\n"))

    println("other user:")
    println(Await.result(newsService.getRecentUserNews("other user"), Duration.Inf).mkString("\n"))
  }
}
