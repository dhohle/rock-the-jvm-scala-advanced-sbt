package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {


  // 1 - parallel collections
  val parList = List(1, 2, 3).par
  //

  val aParVector = ParVector[Int](1, 2, 3)


  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 100000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  println("serial time " + serialTime)

  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println("parallel time " + parallelTime)

  /*
  map-reduce model
  - split the element into chunks - Splitter
  - operation
  - recombine - Combiner
   */
  // map (safe), flatMap (safe), filter (safe), foreach (safe),
  // note: associative <- order dependent
  // reduce (not always safe), fold (not always safe) <- because the functions inside reduce and fold may not be associative
  println(List(1, 2, 3).reduce(_ - _)) // problem (not associative)
  println(List(1, 2, 3).par.reduce(_ - _)) // problem (not associative)
  //
  println(List(1, 2, 3).reduce(_ + _)) // no problem (associative)
  println(List(1, 2, 3).par.reduce(_ + _)) // no problem (associative)

  // synchronization
  var sum = 0 // sum is called simultaneously on multiple occasions, essentially skipping the summation
  (1 to 100000).toList.par.foreach(sum += _) //result from 3 runs: 1470009544, 2126563812, 1540205141; should be: 705082704
  println(sum) // race conditions

  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
  alternatives
  - ThreadPoolTaskSupport - deprecated
  - ExecutionContextTaskSupport(EC)
     */
  aParVector.tasksupport =  new TaskSupport() { // very unlikely to be used
    override val environment: AnyRef = ???

    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???
  }

  // 2 - atomic ops and references
    // atomic runs fully or not at all - it cannot be intercepted by another thread
  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe (read)
  atomic.set(4) // thread-safe write

  val value = atomic.getAndSet(5) // thread-safe combo

  // if the value is 38, then set to 56; otherwise, do nothing
  atomic.compareAndSet(38, 56)

  atomic.updateAndGet(_ + 1)// thread-safe function run; first runs function, then return result
  atomic.getAndUpdate(_ + 1) // thread-safe function run; returns old value, then runs the function

  atomic.accumulateAndGet(12, _ + _)// thread-safe accumulation
  atomic.getAndAccumulate(12, _ + _)// thread-safe accumulation


}
