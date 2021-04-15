package lectures.part5typesystem

import scala.runtime.VolatileByteRef

object StructureTypes extends App {

  // type as a structure

  // structural types
  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close() = println("Closing or whatever")

    def closeSilently() = println("Not making a sound")
  }

  //  def closeQuitely(closeable: HipsterCloseable OR JavaCloseable) // illegal

  type UnifiedCloseable = {
    def close(): Unit
  } // structural type

  def closeQuitely(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuitely(new JavaCloseable {
    override def close(): Unit = println("Closes From JavaCloseable")
  })
  closeQuitely(new HipsterCloseable)

  // TYPE REFINEMENTS

  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  } // enriched JavaCloseable with closeSilently

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes")

    def closeSilently() = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable) = advCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)

  //  closeShh(new HipsterCloseable) // type refinement only for AdvancedJavaCloseable

  // using structural types as standalone types
  def altClose(closeable: {def close(): Unit}): Unit = closeable.close()

  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("Bark")
  }

  class Car {
    def makeSound(): Unit = println("Vrooom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car
  // static duck typing

  // CAVEAT: based on reflection (slowwww)


  /*
  Exercises
   */
  trait CBL[+T] {
    def head: T

    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZZZ!"
  }

  def f[T](somethingWithAHead: {def head: T}): Unit = println(somethingWithAHead.head)

  case object CBNil extends CBL[Nothing]{
    def head: Nothing = ???
    def tail: CBL[Nothing] = ???
  }

  case class CBCons[T] (override val head:T, override val tail:CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human) // ?! T = Brain

  //2.
  object HeadEqualizer {
    type Headable[T] = {def head: T}

    def ===[T](a: Headable[T], b: Headable[T]) = a.head == b.head
  }

  val brainzlist = CBCons(new Brain, CBNil)
  val stringsList = CBCons("Brainz", CBNil)
  HeadEqualizer.===(brainzlist, new Human)
  // problem:
  HeadEqualizer.===(new Human, stringsList) // not type safe



}

