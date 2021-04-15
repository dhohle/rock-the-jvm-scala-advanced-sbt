package lectures.part5typesystem

object FBoundedPolymorphism extends App {




  // Solution 1
//  trait Animal{
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal{
//    override def breed: List[Animal] = ??? // List[Cat]
//  }
//  class Dog extends Animal{
//    override def breed: List[Animal] = ??? // List[Dog]
//  }

  // Solution 2 - FBounded Polymorphism
//  trait Animal[A <: Animal[A]]{// recursive type: F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat]{
//    override def breed: List[Animal[Cat]] = ???
//  }
//
//  class Dog extends Animal[Dog]{
//    override def breed: List[Animal[Dog]] = ???
//  }
//
//  //
//  trait Entity[E <: Entity[E]] // ORM
//  class Person extends Comparable[Person]{ // FBP (F-Bounded Polymorphism)
//    override def compareTo(o: Person): Int = ???
//  }
//
//  class Crocodile extends Animal[Dog]{// which is fine, but wrong (human mistake)
//    override def breed: List[Animal[Dog]] = ???
//  }

  // Solution #3 - FBP + self-types
//  trait Animal[A <: Animal[A]]{ self: A=>// recursive type: F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat]{
//    override def breed: List[Animal[Cat]] = ???
//  }
//
//  class Dog extends Animal[Dog]{
//    override def breed: List[Animal[Dog]] = ???
//  }
////  class Crocodile extends Animal[Dog]{// Must extend Animal[Crocodile], doesn't compile
////    override def breed: List[Animal[Dog]] = ???
////  }
//
//
//  trait Fish extends Animal[Fish]
//  class Shark extends Fish{
//    override def breed: List[Animal[Fish]] = List(new Cod) // Wrong
//  }
//  class Cod extends Fish{
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Exercise

  // Solution 4: type classes
//  trait Animal
//  trait CanBreed[A]{
//    def breed(a:A):List[A]
//  }
//
//  class Dog extends Animal
//  object Dog{
//    implicit object DogsCanBreed extends CanBreed[Dog]{
//      override def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A){
//    def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed // List[Dog
//  /*
//   new CanBreedOps[Dog](dog).breed
//
//   implicit value to pass to breed: Dog.DogsCanBreed
//   */
//
//  class Cat extends Animal
//  object Cat{
//    implicit object CatsCanBreed extends CanBreed[Dog]{ // wrong implementation
//      def breed(a:Dog) : List[Dog] = List()
//    }
//  }
//
//  val cat = new Cat
//  cat.breed //no implicit found parameter canBreed


  // Solution #5
  trait Animal[A] {// pure type classes
    def breed(a:A):List[A]
  }
  class Dog
  object Dog{
    implicit object DogAnimal extends Animal[Dog]{
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal:A){
    def breed(implicit animalTypeClassInstance: Animal[A]):List[A]= animalTypeClassInstance.breed(animal)
  }
  class Cat
  object Cat{
    implicit object CatAnimal extends Animal[Dog]{ // wrong type
      override def breed(a: Dog): List[Dog] = List()
    }
  }
  val dog = new Dog
  dog.breed

  val cat = new Cat
//  cat.breed // error... extends the wrong type
}
