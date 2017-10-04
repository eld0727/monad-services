package go.to.monad.cats

import cats.Id
import cats.data.{OptionT, StateT}


object StateTExample {

  type FibState[A] = StateT[Id, Map[Int, Int], A]

  def fib(n: Int): FibState[Int] = for {
    a <- getOrCalcFib(n - 1)
    b <- getOrCalcFib(n - 2)
  } yield a + b

  def getOrCalcFib(n: Int): FibState[Int] = {
    if(n <= 1) StateT.pure(1)
    else OptionT[FibState, Int](StateT.inspect(_.get(n))).getOrElseF(calcFib(n))
  }

  def calcFib(n: Int): FibState[Int] = for {
    res <- fib(n)
    _ <- StateT.modify[Id, Map[Int, Int]](_.updated(n, res))
  } yield {
    res
  }

  def main(args: Array[String]): Unit = {
    val (state, result) = fib(10).run(Map.empty)
    println(result)
    println(state)
  }

}
