package lectures.part4implicits

object PimpMyLibrary extends App {

  // 2.isPrime
  // Must have one, and only one, parameter
  //  implicit class RichInt(value:Int){
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }

      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] =
        if (n <= 0) List()
        else concatenate(n - 1) ++ list

      concatenate(value)
    }
  }

  //  implicit class RicherInt(richInt: RichInt) {
  //    def isOdd: Boolean = richInt.value % 2 != 0
  //  }

  // type enrichment = pimping
  new RichInt(42).sqrt
  println(42.sqrt)
  println(42.isEven) // compiles as -> new RichInt(42).isEven


  println(1 to 10)

  import scala.concurrent.duration._

  println(3.seconds)

  // compiler doesn't do multiple implicit searches
  //  42.isOdd // is not found

  /*
  Enrich the String class
  - asInt
  - encrypt
    "John" -> (letters plus 2) "Lnjp"
   Enrich Int class
   - times(function)
    3.times(() => ...)
   - *
     3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class richString(value: String) {
    def asInt: Int = Integer.parseInt(value)

    def encrypt(cypher: Int): String = value.map(_.value + cypher).map(_.toChar).mkString

  }

  println("42".asInt + 42)
  println("John".encrypt(2))


  3.times(() => println("Scala"))
  println(4 * List(1, 2))

  // "3" / 4
  implicit def stringToInt(string: String): Int = string.asInt

  println("32" / 4)

  {
    class RichAltInt(value: Int)

    implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

    // danger zone
    implicit def intToBoolean(i: Int): Boolean = i == 1
    /*
    if(n) do something
    else do something else
     */
    val aConditionedValue = if(3) "OK" else "something wrong"
    println(aConditionedValue)

  }

  /*
  Tips:
  - keep type enrichment to implicit class and type classes
  - avoid implicit defs as much as possible
  - package implicits clearly, bring into scope only what you need (when you need it)
  - IF you need conversions, make them as specific as possible
   */
}
