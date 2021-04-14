package lectures.part1as

import scala.util.Try

object DarkSugar extends App {

  // syntax suger #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // write some code
    42
  }
  //  print(description)

  val aTryInstance = Try { // java's try{...}
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }
  val aFunkyInstance: Action = (x: Int) => x + 1

  /// example: Runables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, scala")
  })

  val aSweeterThread = new Thread(() => println("sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // syntax suger #3: the :: and #:: methods are special

  val prependedList = 2 :: List(3, 4)
  // 2.::(List(3,4)) <- not
  // List(3,4).::2 <- rewrites

  // scala spec: last char decides associativity of method
  1 :: 2:: 3:: List(4,5) // <- rewritten
  List(4,5).::(3).::(2).::(1) // as

  class MyStream[T]{
    def -->:(value:T):MyStream[T] = this// actual implementation here

  }

  // if command ends with : <- they are right-associative
  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: Multi-word method naming
  class TeenGirl(name:String){
    def `and then said`(gossip:String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("lilly")
  lilly `and then said` "Scala is so sweet"

  // syntax sugar #5: infix types
  class Composite[A,B]
//  val composite: Composite[Int, String] = ???
  val composite: Int Composite String = ???

  class -->[A,B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply() AND update()!

  // syntax sugar #7: setters for mutable containers
  class Mutable{
    private var internalMember: Int = 0 // private for OO encapsulation
    def member = internalMember// "getter"
    def member_=(value:Int) =
      internalMember = value
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)





}
