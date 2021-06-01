package com.wtx.job014.streaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.jblas.DoubleMatrix
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.wtx.job014.loadDataSql.loadData

/**
 * 实时推荐模块
 * 需求:选择一个电影后,为后序推荐做准备,推荐相似度最高的电影
 */

// 定义一个基准推荐对象
case class Recommendation(mid: Int, score: Double)
// 定义基于LFM电影特征向量的电影相似度列表
case class MovieRecs(mid: Int, recs: Seq[Recommendation])
//定义mysql 数据存pojo
case class MovieRecsFifter(mid: Int, recs: String)
object MoviesLike {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()

    val ratigsFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\ratings.csv"
    val ratingDF = spark.read.format("com.databricks.spark.csv").option("header", true).load(ratigsFile)
    val selectedRatingsDF = ratingDF.select(ratingDF.col("userId"), ratingDF.col("movieId"), ratingDF.col("rating"), ratingDF.col("timestamp"))

    val moviesFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\movies.csv"
    val df2 = spark.read.format("com.databricks.spark.csv").option("header", true).load(moviesFile)
    val moviesDF = df2.select(df2.col("movieID"), df2.col("title"), df2.col("genres"))

    ratingDF.createOrReplaceTempView("ratings")
    moviesDF.createOrReplaceTempView("movies")
    val io_model = MatrixFactorizationModel.load(spark.sparkContext, "model/MovieRecomModelMini")
    //根据电影隐特征,计算电影相似度
    val movieFeatures = io_model.productFeatures.map {
      case (movieID, features) => (movieID, new DoubleMatrix(features))
    }
    //    movieFeatures.foreach(println)
    /**
     * (203663,[-0.465218; 0.080166; 0.153998; -0.284761; -0.619111; -0.264589; -0.114386; -0.566974; 0.196602; -0.411871; 0.333487; 0.170458; 0.035992; -0.067847; 0.318365; -0.378634; -0.068084; -0.043117; 0.001398; 0.154356])
     * (205841,[-0.017846; 0.035627; -0.001909; -0.033363; -0.118237; -0.032174; -0.068731; -0.094932; -0.035120; -0.118537; 0.002239; 0.007509; -0.014812; 0.089073; 0.030029; -0.135938; -0.047033; 0.092383; 0.053817; -0.014157])
     */

    //求笛卡尔积并进行过滤
    val movieRecs = movieFeatures.cartesian(movieFeatures).filter {
      case (a, b) => a._1 != b._1
    }.map { //计算两两向量余弦相似度(夹角)
      case (a, b) => {
        val simScore = this.consinSim(a._2, b._2)
        (a._1, (b._1, simScore))
      }
    }
    //      movieRecs.foreach(println)
    //过滤相似度>0.8的并且按照相似度从高到低排序
//          println("计算排序矩阵,矩阵数量:"+movieRecs.count())
    val movieRecsFifter = movieRecs.filter { _._2._2 > 0.8 }.groupByKey().map {
      case (mid, items) => MovieRecs(mid, items.toList.sortWith(_._2 > _._2).map(x => Recommendation(x._1, x._2)))
    }
    println("矩阵计算完成导入数据")
    val sqlPojo = movieRecsFifter.map(r => {
      var str = ""
      r.recs.foreach(rows => {
        str = str+rows.mid.toString() + "-" + rows.score.toString() + ","
      })
      MovieRecsFifter(r.mid, str)
    })
    /**
     * row =>{
     * var str=""
     * row.recs.foreach(rows=>{
     * str=rows.mid.toString()+"-"+rows.score.toString()+","
     * })
     * }
     */
    loadData.save2MysqlAsDf( spark.createDataFrame(sqlPojo).toDF("movied","recommend_collections"), "moveies_like_recommend_1")

  }
  // 求向量余弦相似度
  def consinSim(movie1: DoubleMatrix, movie2: DoubleMatrix): Double = {
    movie1.dot(movie2) / (movie1.norm2() * movie2.norm2())
  }
}