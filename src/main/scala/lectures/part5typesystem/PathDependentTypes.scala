package lectures.part5typesystem

object PathDependentTypes extends App {

  class Outer{
    class Inner
    object InnerObject
    type InnerType

    def print(i:Inner)= println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod:Int={
    class HelperClass // class inside a method is fine
    type HelperType = String
    2
  }

  // per instance
  val o = new Outer
  val inner = new o.Inner // o.Inner is a TYPE

  val oo = new Outer
//  val otherInner:oo.Inner= new o.Inner // different types of Inner; PathDepenendTypes error
  o.print(inner)
//  oo.print(inner)// illegal, wrong inner

  // Outer#Inner <- common supertype
  o.printGeneral(inner)
  oo.printGeneral(inner)


  /*
  Exercise
  DB keyed by Int or String, but maybe others
   */
  /*
  use path-dependent type
  abstract type members and/or type aliases
   */

  trait ItemLike{
    type Key
  }
  trait Item[K] extends ItemLike{
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key:ItemType#Key):ItemType = ???

  get[IntItem](42) // should compile
  get[StringItem]("home") // should compile
//  get[IntItem]("Scala") // should not be ok


}
