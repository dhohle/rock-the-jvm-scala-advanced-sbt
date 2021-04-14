package lectures.part2afp

import exercises.{EmptySet, MySet}

import scala.annotation.tailrec

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException


  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // a proper (total) function
  // {1,2,5} => Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value

  println(aPartialFunction(2))
  //  println(aPartialFunction(5643))

  // PF (Partial Function) utilities
  println(aPartialFunction.isDefinedAt(67))
  println(aPartialFunction.isDefinedAt(5))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int] ; from partial to total function
  println(lifted(2)) // Some(56)
  println(lifted(97)) // None

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))
  //  println(pfChain(67)) // match error

  // PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99

  }

  // HOFs (Higher Order Functions) accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  println(aMappedList)

  /*
  Note: PF can only have ONE parameter type
   */
  val chatBotFunction: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is bot"
    case "goodbye" => "bye"
    case "call mom" => "unknown mom"
    case _ => s"no "
  }

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5
  }
}

//  scala.io.Source.stdin.getLines().foreach(line=> println(s"chatbox says: ${chatBotFunction(line)}"))
//  scala.io.Source.stdin.getLines().map(chatBotFunction).foreach(println)

object MySet {
  /*
    val s = MySet(1, 2, 3) = buildSet(seq(1,2,3), [])
     = buildSet(seq(2,3), [] + 1)
     = buildSet(seq(3), [1] + 2)
     = buildSet(seq(), [1,2]+3)
     = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    // complains but works
    buildSet(values.toSeq, new EmptySet[A])
  }
}


object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter (_ % 2 ==0) foreach println


  val negative = !s // s.unary_! = all the naturals not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ %2 ==0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))

}