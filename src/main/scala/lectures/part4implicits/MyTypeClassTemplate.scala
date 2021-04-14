package lectures.part4implicits

//TYPE class
trait MyTypeClassTemplate[T] {
  def action(value: T): String
}
/*
  Custom classes
   */

object MyClassTemplate {
  def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
}
