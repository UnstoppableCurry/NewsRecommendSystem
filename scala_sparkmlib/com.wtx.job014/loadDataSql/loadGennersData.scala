package com.wtx.job014.loadDataSql

import com.wtx.job014.movie.ALSRecommendUser4GennerUtil
import org.apache.spark.sql.{ DataFrame, SparkSession }
import shapeless._0
import scala.collection.mutable.ListBuffer

object loadGennersData {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
    var l = new ListBuffer[String]
    for (i <- 2 to 20) {
      var arrayTest2 = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
      var selected = new Array[String](i)
      var str = pazzle.print_zuhe(arrayTest2, selected, l, i, 0)
    }
    println(l.size)
    var hashMap = new scala.collection.mutable.HashMap[String, String];
    l.foreach(row => {
      //      println(row)
      var arr = List[Int]()
      row.split(",").foreach(str => {
        arr = arr :+ str.toInt
      })
      //      println("去重前: "+arr.length)
      //      println("去重后"+arr.distinct.length)
      val a = ALSRecommendUser4GennerUtil.genner4moviesUtil(args(0), arr.toArray, 20)//"model/user_genner"
      var arrStr = ""
      arr.foreach(row => {
        arrStr = arrStr + row.toString() + ","
      })
      var valueStr = ""
      a.foreach(row => {
        valueStr = valueStr + row._1 + "-" + row._2 + ","
      })
      hashMap.put(arrStr, valueStr)
      println(hashMap.size)
    })
    println(hashMap.size)
    val data = spark.createDataFrame(hashMap.toArray).toDF("genners_Collections", "recommends_movies_num")
    data.coalesce(1).write.option("header", "true").csv("./recommend_Demo.csv")
    hashMap.foreach(println)
    hashMap.toArray.foreach(println)
    //      loadData.save2MysqlAsDf(spark.createDataFrame(hashMap.toArray).toDF("genners_Collections", "recommends_movies_num"), "All_gennersrecommend_topten_movies")
    //    loadData.save2MysqlAsDf(spark.createDataFrame(a), "demo")

  }
}