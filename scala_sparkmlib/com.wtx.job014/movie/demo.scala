package com.wtx.job014.movie
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating
import scala.Tuple2
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.network.util.IOMode
object demo {
  def main(args: Array[String]): Unit = {
     Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()

    val io_model = MatrixFactorizationModel.load(spark.sparkContext, args(0))
    val topN = io_model.recommendProducts(args(1).toInt ,args(2).toInt )
    for(rating <- topN){
//      println(rating)
              println(rating.product)
            }
  }
   def getTheTopN(path:String,UserId:Int,TopNumber:Int ):Array[Rating]  = {
      val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()

    val io_model = MatrixFactorizationModel.load(spark.sparkContext, path)
    val topN = io_model.recommendProducts(UserId ,TopNumber)
    return topN
   }
}