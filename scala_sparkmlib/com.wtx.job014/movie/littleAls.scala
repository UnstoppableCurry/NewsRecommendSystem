package com.wtx.job014.movie
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating
import scala.Tuple2
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.network.util.IOMode
import org.apache.spark.mllib.recommendation.ALS

object littleAls {
  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], implicitPrefs: Boolean): Double = {
    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    val predictionsAndRatings = predictions.map { x => ((x.user, x.product), x.rating)
    }.join(data.map(x => ((x.user, x.product), x.rating))).values
    if (implicitPrefs) {
      println("(Prediction, Rating)")
      println(predictionsAndRatings.take(5).mkString("\n"))
    }
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).mean())
  }
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()

    val ratigsFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\little_ratings\\little_ratings.csv"
    val ratingDF = spark.read.format("com.databricks.spark.csv").option("header", true).load(ratigsFile)

    val splits = ratingDF.randomSplit(Array(0.75, 0.25), seed = 12345L)
    val (trainingData, testData) = (splits(0), splits(1))
    val numTraining = trainingData.count()
    val numTest = testData.count()
    println("numTraining 训练集数据条数:	" + numTraining + "  numTest测试集数据条数 :" + numTest)
    //训练集数据条数:	18749614  numTest测试集数据条数 :6250481
    //构建好测试集rdd与训练集rdd后开始为als交替最小二乘法准备rdd数据
    val ratingsRDD = trainingData.rdd.map(
      row => {
        val userId = row.getString(0)
        val movieId = row.getString(1)
        val ratings = row.getString(2)
        Rating(userId.toInt, movieId.toInt, ratings.toDouble)
      })
    val testRDD = testData.rdd.map(row => {
      val userId = row.getString(0)
      val movieId = row.getString(1)
      val ratings = row.getString(2)
      Rating(userId.toInt, movieId.toInt, ratings.toDouble)
    })
    //至此als所需rdd数据集已经准备好
    val rank = 20 //学习次数
    val numIterations = 15
    val lambda = 0.10
    val alpha = 1.00
    val block = -1
    val seed = 12345L
    val implicitPrefs = false
    //als超参设置
        val startTime=System.currentTimeMillis()
            val model=new ALS().setIterations(numIterations).setBlocks(block).setAlpha(alpha).setLambda(lambda).setRank(rank).setSeed(seed).setImplicitPrefs(implicitPrefs).run(ratingsRDD)
            val savedALSModel=model.save(spark.sparkContext, "model/MovieRecomModelMini")
            val endTime=System.currentTimeMillis()
            println("训练模型完毕,用时"+(endTime-startTime)/1000)
            val topN=model.recommendProducts(72315, 10)
            for(rating <- topN){
              println(rating.toString())
            }
        println("----结束----")
  }
}