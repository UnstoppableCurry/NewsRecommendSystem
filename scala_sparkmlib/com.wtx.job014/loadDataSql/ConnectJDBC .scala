
import java.sql.{Connection, DriverManager, SQLException}

object type_name {
  class Student(val id: Int, val name: String, val age: Int) {
  override def toString = "id = " + id + ", name = " + name + ", age = " + age
}
   def main(args: Array[String]): Unit = {
    val url = "jdbc:mysql://localhost:3306/test?user=root&useUnicode=true&characterEncoding=utf-8"
    var conn: Connection = null
    try {
      // load mysql driver
      classOf[com.mysql.jdbc.Driver]
      conn = DriverManager.getConnection(url)
      val stmt = conn.createStatement
      val sql = "select * from student"
      val rs = stmt.executeQuery(sql)
      while (rs.next) {
        println(new Student(rs.getInt(1), rs.getString(2), rs.getInt(3)))
      }
    } catch {
      case e: SQLException => e.printStackTrace
      case e: Exception => e.printStackTrace
    } finally {
      if (conn != null) {
        conn.close
      }
    }
  }
}