package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object MagnetPattern extends App {

  // method overloading

  class P2PRequest

  class P2PResponse

  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T: Serializer](message: T): Int
    def receive[T:Serializer] (message: T, statusCode:Int):Int
    def receive(future:Future[P2PRequest]):Int
//    def receive(future:Future[P2PResponse]):Int // does not compile, same param as P2PRequest
    // lots of overloads
  }

  /*
  1 - type erasure
  2 - lifting doesn't work for all overloads
      val receiveFV = receive _ // Lift to what?

  3 - code duplication
  4 - type inference and default args
      actor.receive(?!)
   */

  trait MessageMagnet[Result]{
    def apply():Result
  }
  def receive[R](magnet:MessageMagnet[R]):R = magnet()//magnet.apply()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {// Int is result type
    override def apply(): Int = {
        // logic for handling P2PRequest
      println("Handling P2P request")
      42
    }
  }
  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {// Int is result type
    override def apply(): Int = {
        // logic for handling P2PResponse
      println("Handling P2P response")
      24
    }
  }


  receive(new P2PRequest)
  receive(new P2PResponse)

  // 1 - no more type erasure problems!
   implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int]{
    override def apply(): Int = 2
  }
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int]{
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2 - lifting
  trait MathLib{
    def add1(x:Int) = x + 1
    def add1(s:String) = s.toInt + 1
    def add1(d:Double):Int  = d.intValue() + 1
  }
  trait AddMagnet{
    def apply() : Int
  }

  def add1(magnet: AddMagnet):Int = magnet()

  implicit class AddInt(x:Int) extends AddMagnet{
    override def apply(): Int = x + 1
  }
  implicit class AddString(x:String) extends AddMagnet{
    override def apply(): Int = x.toInt + 1
  }
  implicit class AddDouble(x:Double) extends AddMagnet{
    override def apply(): Int = x.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1))
  println(addFV("1"))
  println(addFV(41.2d))


//  val receiveFV = receive _
//  receiveFV(new P2PResponse) // doesn't work

  /*
  Drawbacks
  1 - verbose
  2 - harder to read
  3 - you can't name or place default arguments
  4 - call by name doesn't work correctly
   */

  class Handler{
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // other overloads
  }
  trait HandleMagnet{
    def apply():Unit
  }

  def handle(magnet: HandleMagnet) = magnet.apply()

  implicit class StringHandle(s: => String) extends HandleMagnet{
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod():String={
    println("Hello, Scala")
    "print something"
  }

//  handle(sideEffectMethod())
  handle{
    println("Hello, Scala")
    "print something" // Be careful, this is converted to : new StringHandler("print something")
  }

}
