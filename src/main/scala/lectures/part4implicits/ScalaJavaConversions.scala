package lectures.part4implicits

import java.{util => ju}

object ScalaJavaConversions extends App {

  //  import collection.JavaConverters._

  import scala.jdk.CollectionConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala

  /* Java -> Scala
  Iterator
  Iterable
  ju.List - collection.mutable.Buffer
  ju.Set - collection.mutable.Set
  ju.Map - collection.mutable.Map
   */

  import collection.mutable._

  val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer) // same scala object

  val numbers = List(1, 2, 3) // immutable type
  val juNumbers = numbers.asJava // converts to mutable list
  val backToScala = juNumbers.asScala // converts to mutable list
  println(numbers eq backToScala) // false, because numbers is immutable (Java doesn't have immutable)
  println(numbers == backToScala) // true, because the numbers inside the lists are the same

  /*
  create a Scala-Java Optional-Option
   */
  class ToScala[T](value: => T){
    def asScala:T = value
  }
  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if(o.isPresent) Some(o.get()) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption = juOptional.asScala
  println(scalaOption)
  println(ju.Optional.empty().asScala)

}
