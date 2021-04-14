package lectures.part4implicits

object OrganizingImplicits extends App {

  // uses default implicit ordering
  println(List(1,4,5,3,2).sorted)
  // overrides the default implicit ordering
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
   println(List(1,4,5,3,2).sorted)
//  implicit val ordering: Ordering[Int] = Ordering.fromLessThan(_ < _) // another implicit confuses the compiler

  // scala.Predef
  /*
  Implicits (used as implicit parameters):
   - val /var
   - object
   -accessor methods = defs with no parentheses
   */

  // Exercise
  case class Person(name:String, age:Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66),
  )

  object a {
//    implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan(_.name < _.name)  // would not compile
  }

//  object Person  {// works, This is a companion object
//    implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
//  }
//  implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
//  implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name) < 0)
//  println(persons.sorted)

  /*
  Implicit scope
  - normal scope = LOCAL SCOPE
  - imported scope =
  - companions of all types involved in the method signature
    - List
    - Ordering
    - all the types involved = A or any supertype
   */
  // def sorted[B >: A](implicit ord: Ordering[B]): List[B] // default implicit

  /*
  Best practices:
  - When defining an implicit val:
   #1
   - If there is a single possible value for it
   - and you cen edit the code for the type
   Then define the implicit in the companion
   #2
   - if there are many possible values for it
   - but a single good one
   - and you can edit the code for the type
   Then define the good implicit in the companion, and the other elsewhere (local scope, or other object)
   */

  object AlphabeticNameOrdering{
    implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  }

  object AgeOrdering {
    implicit val alphabeticOrdering:Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }
//  import AlphabeticNameOrdering._
  import AgeOrdering._
  println(persons.sorted)

  /*
  Exercise

  3 orderings
  - total price = most used (50%)
  - by unit count
  - by unit price
   */
  case class Purchase(nUnits:Int, unitPrice:Double)
  object Purchase{ // easiest
    implicit val purchaseOrderingTotalPrice:Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.unitPrice*a.nUnits < b.unitPrice*b.nUnits)
  }
  object UnitCountOrdering{
    implicit val purchaseOrderingUnitCount: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }
  object UnitPriceOrdering{
    implicit val purchaseOrderingUnitPrice: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }




}
