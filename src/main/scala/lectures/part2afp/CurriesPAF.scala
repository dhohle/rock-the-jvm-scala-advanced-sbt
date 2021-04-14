package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 +y
  println(add3(5))


  println(superAdder(3)(5))

  // METHOD
  def curriedAdder(x: Int)(y: Int): Int = x + y

  // Lifting (transforming a method to a function)
  // Lifting = ETA-EXPANSION
  val add4: Int => Int = curriedAdder(4)
  //  val add4 = curriedAdder(4) // fails

  // function != methods (JVM limitation)
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA-expansion
  List(1, 2, 3).map(x => inc(x)) // rewrites as

  // Parial function applications
  val add5 = curriedAdder(5) _ // Do an ETA-Expansion, convert expression to Int => Int


  // EXERSIZE
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many different implementations of add7 using the above

  val add7_1 = simpleAddFunction(7, _)
  println(add7_1(8))
  val add7_2 = simpleAddMethod(7, _)
  println(add7_2(9))
  val add7_3: Int => Int = curriedAddMethod(7)
  println(add7_3(2))
  val add7_4 = curriedAddMethod(7) _ // PAF (Partially Applied Function)
  println(add7_4(4))
  val add7_5 = curriedAddMethod(7)(_) // PAF (Partially Applied Function)
  println(add7_5(4))
  //
  val add7 = (x: Int) => simpleAddFunction(7, x) // simplest
  val add7__1 = (x: Int) => simpleAddMethod(7, x) // simplest
  val add7__2 = (x: Int) => curriedAddMethod(7)(x) // simplest

  val add7__3 = simpleAddFunction.curried(7)
  val add7__4 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
  // y => simpleAddMethod(y, 7)


  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?")
  println(insertName("Daniël"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x,y) => concatenator
  println(fillInTheBlanks("Daniël", "Scala is ok"))

  // Exercises
  /*
   1. Process a list of numbers and return their string representation with different formats
      Use the %4.2f, %8.6f, and %14.12f
   */

  def curriedFormatter(s: String)(number: Double) = s.format(number)

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  //%4.2f, %8.6f, and %14.12f
  //  val formatters = List()
  val simpleFormat = curriedFormatter("%4.2f") _ // lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(simpleFormat)
  println(numbers.map(simpleFormat))
  println(seriousFormat)
  println(numbers.map(seriousFormat))
  println(preciseFormat)
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%6.6f"))) // compiler does ETA-expansion

  /*
  difference between
  - functions vs methods
  - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int) = n + 1

  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42
  //
  byName(32) // ok, 32 is an int
  byName(method) // ok, method is simple, and returns an int
  byName(parenMethod()) // ok, returns int
  byName(parenMethod) // ok, but beware ==> byName(parenMethod())
  //  byName(()=>42) // illegal; can't call with a function
  byName((() => 42) ()) // ok, because the param is the called function; so int result
  //  byName(parenMethod _ ) // not ok, function value is not ok
  //
  //  byFunction(45)// not ok, expects lambda
  //  byFunction(method) // not ok, method is compiled to a value; Does not do ETA-expansion, for a method without parenthesis
  byFunction(parenMethod) // compiler does ETA-expansion
  //  byFunction(parenMethod())// not ok, returns a value, not a function
  byFunction(() => 46) // ok, because it is a function returning int
  byFunction(parenMethod _) // ok (but warning), but underscore is not necessary


}
