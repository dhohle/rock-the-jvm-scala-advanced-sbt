package exercises


object EqualityPlayground extends App {

  case class User(name: String, age: Int, email: String)

  val john = User("John", 32, "john@example.com")

  /*
  Equals
   */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) = equalizer.apply(a, b)
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object StringEquality extends Equal[String] {
    override def apply(a: String, b: String): Boolean = a == b
  }


  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }


  /*
  Exercise: implement the TC pattern for the Equality tc
   */
  println(Equal.apply(john, john))
  println(Equal(john, john)) // AD-HOC polymorphism
  println(Equal.apply("john", "johnx"))

  /*
  Exercise - improve the Equal TC with an implicit conversion class
   */

  implicit class TypeSafeEqual[T](a: T) {
    def ===(b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(a, b)

    def !==(b: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.apply(a, b)
  }

  println(john === john)
    /*
    - john.===(anotherJohn)
    - new TypeSafeEqual[User](john).===(anotherJohn)
    - new TypeSafeEqual[User](john).===(anotherJohn)(NameEquality)
    -
     */
  println(john !== john)

  /*
  TYPE SAFE
   */

  println(john == 43) // from Scala
//  println(john === 43) // implicit implementation, does not compile

}
