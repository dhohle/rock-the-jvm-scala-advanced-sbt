package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  val aCondition: Boolean = true
  val aConditionedVal = if (aCondition) 42 else 65

  // instructions vs expressions

  //compile infers types for us
  val aCodeBlock = {
    if (aCondition) 54
    56
  }


  //Unit = void
  val theUnit = println("hello, scala")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack and tail
  @tailrec def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)

  // object-oriented programming
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog // subtyping polymorphism

  //
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // natural language (infix notation)

  1 + 2 // is rewritten
  1.+(2) // as


  //anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar!")
  }

  //generics
  abstract class MyList[+A] // variance and variance problems in THIS course

  // singletons and companions
  object MyList // companion with abstract class MyList

  // case classes
  case class Person(name: String, age: Int) // serializable, fields, ...

  //exceptions and try/catch/finally
  val throwsException = throw new RuntimeException // type is Nothing

  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  // packaging and imports

  // functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1

  // functions as first-class elements

  List(1, 2, 3).map(anonymousIncrementer)
  // map, flatMap, filter
  //for comprehension
  val pairs = for {
    num <- List(1, 2, 3)
    char <- List('a', 'b', 'b')
  } yield num + "-" + char


  // Scala collections: Seq, Array, List, Vector, Map, Tuple
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "collections": Option, Try
  val anOption = Some(2) // None

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }

  // all the patterns


}
