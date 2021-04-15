package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {

  /*
  Users, posts, feeds
  Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
  1 - intermediate data types: Int, String, List, Date
  2 - type classes for conversion to intermediary data types
  3 - serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
    {
    name: "John",
    age: 22.
    friends: [ ... ]
    latestPost:{

    }
     */
    override def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniël"),
    "post" -> JSONArray(List(
      JSONString("Scala rocks"),
      JSONNumber(456)
    ))
  ))

  println(data.stringify)

  // type class
  /*
  1 - type class
  2 - type class instances (implicit)
  3 - pimp library to use type class instances
   */

  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }


  //2.3 conversion
  implicit class JSONOps[T](value:T){
    def toJSON(implicit converter: JSONConverter[T]) :JSONValue=
      converter.convert(value)
  }

  //2.2
  implicit object StringConverter extends JSONConverter[String]{
    override def convert(value: String): JSONValue = JSONString(value)
  }
  implicit object NumberConverter extends JSONConverter[Int]{
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  //custom data type
  implicit object UserConverter extends JSONConverter[User]{
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit  object PostConverter extends JSONConverter[Post]{
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "createAt" -> JSONString(post.createAt.toString)
    ))
  }

  implicit  object FeedConverter extends JSONConverter[Feed]{
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
//      "user" -> UserConverter.convert(feed.user), // TODO be nicer
//      "feed" -> JSONArray(feed.posts.map(PostConverter.convert)) // TODO be nicer
      "user" -> feed.user.toJSON,
      "feed" -> JSONArray(feed.posts.map(_.toJSON)) // TODO be nicer
    ))
  }


  // call stringify on results
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@rtjvm.com")
  val feed = Feed(john, List(
    Post("Hello", now),
    Post("Look at this puppy", now)
  ))

  println(feed.toJSON.stringify)




}
