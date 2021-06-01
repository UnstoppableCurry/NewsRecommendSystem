package com.wtx.job014.loadDataSql
import java.sql.{Connection, DriverManager, SQLException}
class Student(val id: Int, val name: String, val age: Int) {
}
object mysqldemo {
  def main(args: Array[String]): Unit = {
    
    val url = "jdbc:mysql://192.168.1.9:3306/deeplearning4scala?user=root&password=1f4e7e3217016745&useUnicode=true&characterEncoding=utf-8"
    var conn: Connection = null
    try {
      // load mysql driver
      classOf[com.mysql.jdbc.Driver]
      conn = DriverManager.getConnection(url)
      val stmt = conn.createStatement
      val sql = "select * from movies"
      val rs = stmt.executeQuery(sql)
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