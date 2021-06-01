package com.wtx.job014.streaming

import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.jblas.DoubleMatrix
import org.apache.spark.ml.feature.{ HashingTF, IDF, Tokenizer }
import org.apache.spark.ml.linalg.SparseVector
import com.wtx.job014.loadDataSql.loadData

/**
 * 冷启动模块:
 * 需求:解决冷启动时推荐选择
 * 方法:基于item-cf物品推荐(标签)
 * 场景:用户点击标签后,针对标签进行推荐
 */
object ColdStart2 {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
    val moviesFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\movies.csv"
    val df2 = spark.read.format("com.databricks.spark.csv").option("header", true).load(moviesFile)
    val moviesDF = df2.select(df2.col("movieID"), df2.col("title"), df2.col("genres"))
//        import spark.implicits._ //隐式转换直接转换df类型,不然会报错
     val dataframes = moviesDF.rdd.map(row=>{
       	val movieID = row.getString(0)
       	val title = row.getString(1)
       	val genres = row.getString(2).replace("|", " ")
       	(movieID,title,genres)
     })
     spark.createDataFrame(dataframes).show(false)
//    val dataframes =  moviesDF.rdd.map(row => (row.getAs[Int]("movieID"), row.getAs[String]("title"), row.getAs[String]("genres").map(c => if (c == '|') ' ' else c)))
//    将genners 的|用空格替换
//    val dataframes2 = df2.toDF("movieID", "title", "genres").map(row => (row.getAs[Int]("movieID"), row.getAs[String]("title"), row.getAs[String]("genres").map(c => if (c == '|') ' ' else c)))
    // 创建分词器,按照空格分词
//    dataframes2.show(false)
}}