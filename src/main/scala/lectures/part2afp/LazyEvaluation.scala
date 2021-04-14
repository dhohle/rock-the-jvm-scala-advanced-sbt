package lectures.part2afp

object LazyEvaluation extends App {

  // lazy values are evaluated once, only when called
  {
    //  val x: Int = throw new RuntimeException// throws exception, right away
    lazy val x: Int = throw new RuntimeException // throws exception when used
    //    println(x) // throws exception
  }

  lazy val x: Double = {
    println("hello")
    Math.random()
  }
  //  println(x) // prints a random double
  //  println(x) // prints the same double

  lazy val y: Double = {
    println("hello")
    Math.random()
  }
  //  println(y) // prints a random double
  //  println(y) // prints the same double


  // example of implications:
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  // since simpleCondition is false, lazyCondition need not be executed, so Boo is not printed
  //  println(if (simpleCondition && lazyCondition) "yes" else "no")
  //  println("xxx")
  //  println(if (lazyCondition && simpleCondition) "yes" else "no") // result is no, but lazyCondition IS evaluated, because it is BEFORE simpleCondition

  // in conjunction with call by name
  { // in this case, n is evaluated three times...
    def byNameMethod(n: => Int): Int = n + n + n + 1

    def retrieveMagicValue = {
      // side effect, or long computation
      Thread.sleep(1000)
      println("waiting")
      42
    }

    //    println(byNameMethod(retrieveMagicValue))
  }
  { // use lazy vals instead; now sleep is waiting once, instead of three times
    def byNameMethod(n: => Int): Int = {
      lazy val t = n
      t + t + t + 1
    }

    def retrieveMagicValue = {
      // side effect, or long computation
      Thread.sleep(1000)
      println("waitingx2")
      42
    }

    //    println(byNameMethod(retrieveMagicValue))

  } // called: CALL BY NEED

  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // should return list of less than 30
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)
  println("---------")
  val lt30lazy = numbers.withFilter(lessThan30) /// lazy vals under the hood
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println(gt20lazy)
  gt20lazy.foreach(println)


  // for-comprehensions use withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0 // use lazy vals!
  } yield a + 1
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)

  // implement a lazily evaluated, sinlgy linked STREAM of elements. (the head of the stream is always evaluated, the tail is lazy)
  abstract class MyStream[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // prepend operator

    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenate two streams

    def foreach(f: A => Unit): Unit

    def map[B](f: A => B): MyStream[B]

    def flatMap[B](f: A => MyStream[B]): MyStream[B]

    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of the stream

    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }





}

