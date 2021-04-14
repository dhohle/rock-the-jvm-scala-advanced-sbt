package lectures.part1as

object AdvancedMatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ =>
  }

  /*
  - constants
  - wildcards
  - case classes
  - tuples
  - some special magic (like above)
   */
  // with case class
  case class PersonCase(val name: String, val age: Int)

  val bobCase = PersonCase("Bob", 25)
  val greetingCase = bobCase match {
    case PersonCase(n, a) => s"Hi, my name is $n and I am $a yo."
  }

  //  println(greetingCase)

  // without case class:

  class Person(val name: String, val age: Int)

  // companion object; needed for pattern matching if, for some reason, the class Person cannot be a case class
  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a yo."
    case _ => "Not an accepted Person"
  }

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(greeting)
  println(legalStatus)

  /**
   * Exercise.
   */
  val n: Int = 45
  val mathProperty = n match {
    case x if (x < 10) => "single digit"
    case x if (x % 2 == 0) => "an even number"
    case _ => "no property"
  }

  object even {
    //    def unapply(arg: Int): Option[Boolean] =
    //      if (arg % 2 == 0) Some(true)
    //      else None
    def unapply(arg: Int) = (arg % 2 == 0)
  }

  object singleDigit {
    //    def unapply(arg: Int): Option[Boolean] = if (arg < 10 && arg > -10) Some(true) else None
    def unapply(arg: Int) = (arg < 10 && arg > -10)
  }

  val mathClassProperty = n match {
    //    case singleDigit(_) => "single digit"
    //    case even(_) => "an even number"
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"

  }

  println(mathProperty)
  println(mathClassProperty)

  // infix patterns
  case class Or[A, B](a: A, b: B) // Either
  val either = Or(2, "two")
  val humanDescription = either match {
    //    case Or (number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }

  println(humanDescription)

  // decomposing sequences
  val varArgs = numbers match {
    case List(1, _*) => "starting with 1"
  }

  // unapply sequence
  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  //custom return types for unapply
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper{
    def unapply(person:Person) : Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get:String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An Alien"
  })


}
