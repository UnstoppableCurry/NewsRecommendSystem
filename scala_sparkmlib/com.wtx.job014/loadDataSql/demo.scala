package com.wtx.job014.loadDataSql
import com.wtx.job014.movie.ALSRecommendUser4GennerUtil
import org.apache.spark.sql.{ DataFrame, SparkSession }
object demo {
  
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
      val arr=Array(1)
    val a =ALSRecommendUser4GennerUtil.getArrayTheRecommendList4Genners("model/user_genner",arr, 1)
    val b =ALSRecommendUser4GennerUtil.getArrayTheRecommendList4Genners("model/user_genner2_movies",arr, 1)
    b.foreach(println)
    a.foreach(println)  
    var arrStr=""
      arr.foreach(row=>{
        arrStr=arrStr+row.toString()+","
      })
      var valueStr=""
//    a.foreach(row=>{
//     valueStr=valueStr+row._1+"-"+row._2 +","
//    })
            var hashMap = new scala.collection.mutable.HashMap[String, String];
hashMap.put(arrStr, valueStr)
hashMap.foreach(println)
    //    loadData.save2MysqlAsDf(spark.createDataFrame(a), "demo")

  }
}

