package lectures.part5typesystem

object RockingInheritence extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    // some methods
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // Diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal {
    override def name: String = "Lion"
  }

  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }

  //  trait Mutant extends Lion with Tiger
  class Mutant extends Lion with Tiger
//  {
//    override def name: String = "Alien"
//  }

  val mutant = new Mutant

  /*
  Mutant
  extends Animal with {override def name: String = "Lion"}
  with Animal with override def name: String = "Tiger"

  LAST OVERRIDE GETS PICKED
   */

  println(mutant.name) // tiger; because the last override always gets picked

  // the super problem + type linearization
  trait Cold {
    def print:Unit = println("cold")
  }

  trait Green extends Cold{
    override def print: Unit = {
      println("Green")
      super.print
    }
  }
  trait Blue extends Cold{
    override def print: Unit = {
      println("Blue")
      super.print
    }
  }
  class Red{
    def print:Unit = println("Red")
  }
  class White extends Red with Green with Blue{
    override def print: Unit = {
      println("White")
      super.print
    }
  }

  /*
  Cold = AnyRef with <Cold>
  Green
    = Cold with <Green>
    = AnyRef with <Cold> with <Green>
  Blue
    = Cold with <Blue>
    = AnyRef with <Cold> with <Blue>
  Red = AnyRed with <Red>
  //
  White = Red with Green with Blue with <White>
    = AnyRef with <Red>
      with (AnyRed with <Cold> with <Green>) // AnyRef is ignored, because it's seen before
      with (AnyRed with <Cold> with <Blue>) // AnyRef and Cold are ignored, because it's seen before
      with <White>
      ---------vvvvvv--------- Type Linearization
    = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
    so prints: White, Blue, Green, cold
    Red is not printed
   */
  val color = new White
  println(color.print)

}
