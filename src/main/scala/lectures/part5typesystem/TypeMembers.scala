package lectures.part5typesystem

object TypeMembers extends App {

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // upperbouded - with Animal
    type SuperBoundedAnimal >: Dog <: Animal // superbounded in Dog, upperbounded in Animal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
//  val dog: ac.AnimalType = ??? // can't really do anything with this

  //  val cat: ac.BoundedAnimal = new Cat // compiler doesn't now what bounded Animal this is, and so doesn't compile
  val pu: ac.SuperBoundedAnimal = new Dog // fine
  val cat: ac.AnimalC = new Cat // fine

  type CatAlias = Cat // type of cat

  val anotherCat: CatAlias = new Cat

  trait MyList {
    type T

    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int

    def add(element: Int): MyList = this
  }

  // .type
  type CatsType = cat.type // type alias
  val newCat: CatsType = cat
  //  new CatsType //class type required but cat.type found   new CatsType


  /*
  Exercise - enforce a type to be applicable to SOME TYPES only
   */

  //LOCKED
//  trait MList {
//    type A
//
//    def head: A
//
//    def tail: MList
//  }
//
//  trait ApplicableToNumbers{
//    type A <: Number
//  }

//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//
//    def head: String = hd
//
//    def tail = tl
//  }
//  class IntList(hd: Int, tl: IntList) extends MList with ApplicableToNumbers{
//    type A = Int
//
//    def head: Int = hd
//
//    def tail = tl
//  }

  // Number
  // type members and type member constraints (bounds)


}
