package lectures.part4implicits

object ImplicitsIntro extends App{

  val pair = "Daniel" -> "555"
  val intPair = 1 -> 2

  case class Person(name:String){
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(str:String): Person = Person(str)
  // greet is not an element of String. But it looks for some implicit method that fulfils this call
  // in this case, Scala finds the fromStringToPerson method to convert a String into a Person which has the method greet
  println("Peter".greet)

  val p:Person = "Peter"
  println(p.greet)

  {
    class A {
      def greet: Int = 2
    }

    implicit def fromStringToA(str: String): A = new A
    val x:Person = "x" // using implicit to case to person
    val y:A = "x" //using implicit to case to A
//    "x".greet // doesn't know what to convert to, because there are 2 options
  }

  // implicit parameters
  def increment(x:Int) (implicit amount:Int) = x + amount
  implicit val defaultAmount = 10
  increment(2) // uses the implicit default amount
  increment(2)(3) // overrides the default amount
  // NOT default args



}
