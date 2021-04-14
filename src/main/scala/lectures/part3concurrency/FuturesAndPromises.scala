package lectures.part3concurrency

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

//important for Futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesAndPromises extends App {

  def calculuateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculuateMeaningOfLife // calculates the meaning of life on another thread
  } // (global) which is passed by the compiler


  //  println(aFuture.value) // Option[Try[Int]] -> None (if not calculated)

  println("waiting on the future")
  //  aFuture.onComplete(t => t match {
  //    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
  //    case Failure(exception) => println(s"I have failed with $exception")
  //  })
  // with sugar
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(exception) => println(s"I have failed with $exception")
  } // SOME thread (we don't know which; don't make assumptions)

  //  Thread.sleep(3000)

  // part 2
  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // database
    val names = Map(
      "fb.is.1-zuck" -> "Mark",
      "fb.is.2-bill" -> "Bill",
      "fb.is.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.is.1-zuck" -> "fb.is.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching...
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark poke bill
  val mark = SocialNetwork.fetchProfile("fb.is.1-zuck")

  //  mark.onComplete{
  //    case Success(markProfile) => {
  //      val billFuture = SocialNetwork.fetchProfile("fb.is.2-bill")
  //      billFuture.onComplete{
  //        case Success(billProfile) => markProfile.poke(billProfile)
  //        case Failure(e) => e.printStackTrace()
  //      }
  //    }
  //    case Failure(e) => e.printStackTrace()
  //  }


  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name) //  Future[Profile] ->  Future[String]

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val zucksBestFriendsRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions;
  for { // waits for Futures (don't know how, yet)
    mark <- SocialNetwork.fetchProfile("fb.is.1-zuck")
    bill <- SocialNetwork.fetchProfile("fb.is.2-bill")
        } mark.poke(bill)


  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // if the first fails, the second is called. If the second fails, the error of the first is reported
  val fallbackResult = SocialNetwork.fetchProfile("unknown").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


  def banking: Unit = {
    // online banking app
    case class User(name: String)

    case class Transaction(sender: String, receiver: String, amount: Double, status: String)

    object BankingApp {
      val name = "Rock the JVM banking"

      def fetchUser(name: String): Future[User] = Future {
        // simulate fetching from the DB
        Thread.sleep(500)
        User(name)
      }

      def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
        // simulate some processes
        Thread.sleep(1000)
        Transaction(user.name, merchantName, amount, "SUCCESS")
      }

      def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
        // fetch the user from the DB
        // create a transaction
        // WAIT for the transaction to finish
        val transactionStatusFuture = for {
          user <- fetchUser(username)
          transaction <- createTransaction(user, merchantName, cost)
        } yield transaction.status
        // import seconds from 'import scala.concurrent.duration._'
        Await.result(transactionStatusFuture, 2.seconds) // implicit conversion -> pimp my library
      }
    }

    println(BankingApp.purchase("Daniël", "iPhone 12", "rock the JVM store", 3000))

    // promises
    val promise = Promise[Int]() // "controller" over the future
    val future = promise.future

    // thread 1 - consumer
    future.onComplete {
      case Success(r) => println("[consumer] I've received " + r)
    }

    // thread 2 - producer
    val producer = new Thread(() => {
      println("[producer] crunching numbers...")
      Thread.sleep(1000)
      // "fulfilling"the promise
      promise.success(42)

      //    promise.failure(e)
      println("[producer] done")
    })
    producer.start()
  }

  //1 - fulfil immediately
  def fulfilImmediately[T](value: T): Future[T] = Future(value)

  // 2 - in sequence (returns second Future when first is finished
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second)
  }

  //3 - first out of two future
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // 4 - last out of the two futures
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    // the first that succeeds, results in None
    val checkAndComplete = (result: Try[A]) => if (!bothPromise.tryComplete(result)) lastPromise.complete(result) // else None/false

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)

  // 5 - retry until
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action() // try action
      .filter(condition) // return action if condition is true;
      .recoverWith { // if condition is false, recover
        case _ => retryUntil(action, condition)
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextvalue = random.nextInt(100)
    println("generate next value " + nextvalue)
    nextvalue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => println("settled at " + result))


  Thread.sleep(10000)
}
