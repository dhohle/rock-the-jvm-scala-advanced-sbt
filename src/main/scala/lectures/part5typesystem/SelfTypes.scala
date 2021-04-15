package lectures.part5typesystem

object SelfTypes extends App {

  // requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist => // SELF TYPE, whoever implements Single has to implement Instrumentalist
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  //  class Vocalist extends Singer{ //Illegal inheritance, self-type Vocalist does not conform to Instrumentalist
  //    override def sing(): Unit = ???
  //  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(Guitar Solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = "Singer"
  }


  // Self types are compared with inheritance
  // B is a A
  class A

  class B extends A

  // S requires a T
  trait T

  trait S {
    self: T => }

  // CAKE PATTERN => "dependency injection"


  // Java based dependency injection
  trait Component {
    // API
  }

  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // Scala alternative to Dependency Injection = Cake Pattern
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent {
    self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this works!"
  }

  trait ScalaApplication {
    self: ScalaDependentComponent => }

  //layer 1 - small components
  trait Picture extends ScalaComponent

  trait Stats extends ScalaComponent

  //layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture

  trait Analytics extends ScalaDependentComponent with Stats

  //layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics


  // cyclical dependency
  //  class X extends Y // Illegal cyclic
  //  class Y extends X // reference

  trait X {self: Y => }// not cyclic, more brother/sister ref
  trait Y {self: X => }


}
