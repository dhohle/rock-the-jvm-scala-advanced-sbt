package lectures.part5typesystem

import java.time.DayOfWeek

object Variance extends App{

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // What is variance>
  // "inheritance" - type substitutions of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage : CCage[Animal] = new CCage[Cat]

  // no -invariance
  class ICage[T]
//  val icage:ICage[Animal] = new ICage[Cat]

  // "hell no"- opposite = contravariance
  class XCage[-T]
  val xcage:XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal:T)// COVARIANT POSITION

//  class ContravariantCage[-T](val animal T) // doesn't compile
  /*
  val catCage:XCage[Cat] = new XCage[Animal] (new Crocodile)
   */

//  class CovariantVariableCage[+T](var animal:T)// covariant T occurs in contravariant position for var animal
  /*
  var animal:T is illegal, because then it's possible to add a different kind of animal after the class is instantiated.
  Whereas with val, the value cannot be changed, so then it's fine

  val ccage: CCage[Animal] = new CCage[Cat](new Cat)
  ccage.animal = new Crocodile // <- possibility for this, makes it illegal
   */

  // same problem as above, but reversed
//    class CovariantVariableCage[-T](var animal:T)// contravariant T occurs in covariant position for var animal

  class InvariantVariableCage[T](var animal:T) // here var is fine, because there is no restriction on which instance of T is used.

//  trait AnotherCovariantCage[+T]{
//    def addAnimal(animal:T) // CONTRAVARIANT POSITION
//  }
  /*
  val ccage:CCage[Animal] = new CCage[Dog]
  ccage.add(new Cat)
   */

  class AnotherContravariantCage[-T]{
    def addAnimal(animal:T) =true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
//  acc.addAnimal(new Dog) // can't add Dog to Cat cage
  acc.addAnimal(new Cat) // sure
  class Kitty extends Cat
  acc.addAnimal(new Kitty) // Kitty is a Cat

  class MyList[+A]{
    def add[B >: A](element: B):MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)// list of Kitty
  val moreAnimals = animals.add(new Cat) // List of Cat: would be illegal without [B >: A] declaration; the add method now return a list of Cat's, instead of Kitty's
  val evenMoreAnimals = animals.add(new Dog)// List of Animal

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION.

  /// return types
  class PetShop[-T]{
//    def get(isItAPuppy:Boolean):T// METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
    val catShop = new PetShop[Animal]{
      def get(isItaPuppy:Boolean): Animal = new Cat
     }

     val dogShop:PetShop[Dog] catShop
     dogShop.get(true) /// would return a cat

     */

    // should return a subtype instead
    def get [S <: T](isItAPuppy:Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop:PetShop[Dog] = new PetShop[Animal]
  // inferred type arguments [Cat] do not conform to method get's type parameter bounds [S <: lectures.part5typesystem.Variance.Dog]
//  val evilCat = shop.get(true, new Cat)

  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)// fine


  /*
  Big rule
  - method arguments are in CONTRAVARIANT position
  - return types are in COVARIANT position
   */

  /**
   * Invariant, covariant, contravariant Parking[T](things List[T])
   */
  class Vehicle
  class Bike extends Vehicle
  class car extends Vehicle

  class IList[T]

  // Invariant: Accepts only one type of Vehicle
  class IParking[T](vehicle: List[T]){
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]):IParking[T] = ???
    def checkVehicles(conditions:String):List[T] = ???

    def flatMap[S] (f: T => IParking[S]):IParking[S] = ???
  }

  // Covariant: Accepts all subclasses of T
  class CParking[+T] (vehicles:List[T]){
    def park[S >: T](vehicle: S):CParking[S] = ???
    def impound[S >: T](vehicle: List[S]):CParking[S] = ???
    def checkVehicles(conditions:String):List[T] = ???

    def flatMap[S] (f: T => CParking[S]):CParking[S] = ???
  }

  // contravariance
  class XParking[-T] (vehicles:List[T]){
    def park(vehicle: T):XParking[T] = ???
    def impound(vehicle: List[T]):XParking[T] = ???
    def checkVehicles[S <: T](conditions:String):List[S] = ???

    // because covariant, because it is double contravariant
    def flatMap[R <: T, S] (f: Function1[R, XParking[S]]):XParking[S] = ???
  }

  /*
  Rule of thumb
  - use covariance = COLLECTION OF THINGS
  - use contravariance = GROUP OF ACTIONS
   */

  // Covariant: Accepts all subclasses of T
  class CParking2[+T] (vehicles:IList[T]){
    def park[S >: T](vehicle: S):CParking2[S] = ???
    def impound[S >: T](vehicle: IList[S]):CParking2[S] = ???
    def checkVehicles[S >: T](conditions:String):IList[S] = ???
  }

  // contravariance
  class XParking2[-T] (vehicles:IList[T]){
    def park(vehicle: T):XParking2[T] = ???
    def impound[S <: T](vehicle: IList[S]):XParking2[S] = ???
    def checkVehicles[S <: T](conditions:String):IList[S] = ???
  }

  // flatMap
}
