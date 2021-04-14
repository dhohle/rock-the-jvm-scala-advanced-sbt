package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  def firstMethod(): Unit = {
    /*
  interface Runnable {
    public void run()
  }
   */
    // JVM Threads
    val runnable = new Runnable {
      override def run(): Unit = println("Running in parallel")
    }
    val aThread = new Thread(runnable)
    // create a JVM thread => OS Thread
    aThread.start() // gives a signal to the JVM to start a JVM Thread

    //  runnable.run() // calls the method run in Runnable, does not do anything in parallel

    aThread.join() // blocks until aThread finishes running

    //
    val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
    val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))

    threadHello.start()
    threadGoodbye.start()
    // different runs produce different results!

    // executors
    val pool = Executors.newFixedThreadPool(10)
    pool.execute(() => println("something in the thread pool"))
    pool.execute(() => {
      Thread.sleep(1000)
      println("done after 1 second")
    })
    pool.execute(() => {
      Thread.sleep(1000)
      println("Almost done")
      Thread.sleep(1000)
      println("done after 2 seconds")
    })

    pool.shutdown()
    //  pool.execute(()=>println("no")) // throws an exception in the calling thread

    //  pool.shutdownNow() // interrupts already running threads
    println(pool.isShutdown)


    def runInParallel = {
      var x = 0

      val thread1 = new Thread(() => {
        x = 1
      })

      val thread2 = new Thread(() => {
        x = 2
      })

      thread1.start()
      thread2.start()

      println(x)
    }

    for (_ <- 1 to 10000) runInParallel
    // race condition

  }

  def secondMethod(): Unit = {

    class BankAccount(var amount: Int) {
      override def toString: String = "" + amount
    }

    def buy(account: BankAccount, thing: String, price: Int) = {
      account.amount -= price
      //    println("I bought " + thing)
      //    println("my account is now " + account)
    }

    for (_ <- 1 to 100000) {
      val account = new BankAccount(50000)
      val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
      val thread2 = new Thread(() => buySafe(account, "iPhone12", 4000))
      thread1.start()
      thread2.start()
      Thread.sleep(100)
      if (account.amount != 43000)
        println("AHA: " + account.amount)
      //    println("------")

    }

    // option #1: use synchronized
    def buySafe(account: BankAccount, thing: String, price: Int) =
      account.synchronized {
        // no two threads can evaluate this at the same time
        account.amount -= price
        println("I bought " + thing)
        println("my account is now " + account)
      }

    //option #2: use @volatile
    class BankAccountVolatile(@volatile var amount: Int)

  }

  /*
  1) Construct 50 "inception" threads
   */
  def makeThread(n: Int, max: Int): Option[Thread] = {
    val t = new Thread(() => {
      val opt = makeThread(n + 1, max)
      if (opt.isDefined) {
        val t = opt.get
        t.start()
        t.join()
      }

      println(s"#$n")
    })
    if (n < max) Option(t) else None
  }

//  makeThread(0, 50).get.start()


  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = inceptionThreads(maxThreads, i + 1)
      newThread.start()
      newThread.join()

    }
    println(s"Hello from thread $i")
  })

    inceptionThreads(50).start()
  /*
  2)
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1).start())
  println(x)
  /*
  1) What is the biggest value for x? 100
  2) what is the SMALLEST value possible for x? 1 <- all can start simultaneously

   */


  /*
  3) sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala Sucks"
  awesomeThread.start()
  Thread.sleep(2000)
  println(message)
  /*
  What is the value of the message? almost always "Scala is awesome" <- but it not guaranteed
  Why Not?

  (main thread)
    message = "Scala is awesome"
    awesomeThread.start()
    sleep() - relieves execution
  (awesome thread)
    sleep() - relieves execution
  (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
  (OS gives the CPU back to the MAIN thread)
    println("Scala sucks")
  (OS gives the CPU to awesomethread)
    message = "Scala is awesome"
    (message already printed)
  */

  /*
  How to fix this?
  Synchronizing won't work
  use `thread.join()` instead

   */
}
