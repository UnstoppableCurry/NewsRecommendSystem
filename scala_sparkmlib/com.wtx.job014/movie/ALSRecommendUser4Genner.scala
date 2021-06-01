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
import java.util.HashMap
import scala.collection.mutable.ArrayBuffer

object ALSRecommendUser4Genner {
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

    val ratigsFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\ratings.csv"
    val ratingDF = spark.read.format("com.databricks.spark.csv").option("header", true).load(ratigsFile)
    val selectedRatingsDF = ratingDF.select(ratingDF.col("userId"), ratingDF.col("movieId"), ratingDF.col("rating"), ratingDF.col("timestamp"))

    val moviesFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\movies.csv"
    val df2 = spark.read.format("com.databricks.spark.csv").option("header", true).load(moviesFile)
    val moviesDF = df2.select(df2.col("movieID"), df2.col("title"), df2.col("genres"))

    val id_genner = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\id_genner.csv"
    val df3 = spark.read.format("com.databricks.spark.csv").option("header", true).load(id_genner)
    val id_gennerDF = df3.select(df3.col("ID"), df3.col("Genner"))
    id_gennerDF.createOrReplaceTempView("genner")
    ratingDF.createOrReplaceTempView("ratings")
    moviesDF.createOrReplaceTempView("movies")
    val mostActiveUsersSchemaRDD = spark.sql("SELECT ratings.userId, ratings.movieId,"
      + "ratings.rating, movies.title , explode(split(movies.genres, '[|]')) as genres FROM ratings JOIN movies "
      + "ON movies.movieId=ratings.movieId ")
    mostActiveUsersSchemaRDD.show(false)
    mostActiveUsersSchemaRDD.createOrReplaceTempView("demo")
    val genner = spark.sql("SELECT * from demo JOIN genner ON demo.genres=genner.Genner ")
    genner.createOrReplaceTempView("final")
    val finalTable = spark.sql("select ID,userId,rating from final")
    finalTable.show(false)

    val splits = finalTable.randomSplit(Array(0.75, 0.25), seed = 12345L)
    val (trainingData, testData) = (splits(0), splits(1))
    val numTraining = trainingData.count()
    val numTest = testData.count()
    println("numTraining 训练集数据条数:	" + numTraining + "  numTest测试集数据条数 :" + numTest)
    //训练集数据条数:	18749614  numTest测试集数据条数 :6250481
    //构建好测试集rdd与训练集rdd后开始为als交替最小二乘法准备rdd数据
    val ratingsRDD = trainingData.rdd.map(
      row => {
        val ID = row.getString(0)
        val userID = row.getString(1)
        val rating = row.getString(2)
        Rating(ID.toInt, userID.toInt, rating.toDouble)
      })
    val testRDD = testData.rdd.map(row => {
      val ID = row.getString(0)
      val userID = row.getString(1)
      val rating = row.getString(2)
      Rating(ID.toInt, userID.toInt, rating.toDouble)
    })
    //    //至此als所需rdd数据集已经准备好
    //    val rank = 20 //学习次数
    //    val numIterations = 15
    //    val lambda = 0.10
    //    val alpha = 1.00
    //    val block = -1
    //    val seed = 12345L
    //    val implicitPrefs = false
    //als超参设置
    //    val startTime = System.currentTimeMillis()
    //    val model = new ALS().setIterations(numIterations).setBlocks(block).setAlpha(alpha).setLambda(lambda).setRank(rank).setSeed(seed).setImplicitPrefs(implicitPrefs).run(ratingsRDD)
    //    val savedALSModel = model.save(spark.sparkContext, "model/user_genner")
    //    val endTime = System.currentTimeMillis()
    //    println("训练模型完毕,用时" + (endTime - startTime) / 1000)
    //    val topN = model.recommendProducts(8, 10)
    //    for (rating <- topN) {
    //      println(rating.toString())
    //    }
    //    println("----结束----")
    /**
     *  val movie_model = MatrixFactorizationModel.load(spark.sparkContext, "model/MovieRecomModel")
     * var i=0
     * for (rating <- topN) {
     * i=i+1
     * println("第一次推荐:----"+rating)
     * val movies = movie_model.recommendProducts(rating.product ,5)
     * for (a <- movies){
     * println(a+"-------"+a.product)
     * }
     * }
     */
    val user_Reconmend_model = MatrixFactorizationModel.load(spark.sparkContext, "model/user_genner")
    val topN = user_Reconmend_model.recommendProducts(8, 10)
    var z:Array[Int] = new Array[Int](50)
    val l = List(1,2,3).foreach(row=>{
              val topN = user_Reconmend_model.recommendProducts(row.toInt, 10)
              for(a <- topN){
                z=z:+a.product
              }
    })
        z.foreach(row=>println(row+"去重前"))
    z.distinct.foreach(row=>println(row+"去重后"))


    //    val rmseTest = computeRmse(user_Reconmend_model, testRDD, true)
    //    println("testRMSE:  	=>   " + rmseTest)
//    val movie_model = MatrixFactorizationModel.load(spark.sparkContext, "model/MovieRecomModel")
//      var z:Array[Int] = new Array[Int](50)
//    for (rating <- topN) {
//      println("第一次推荐:----" + rating)
//      val movies = movie_model.recommendProducts(rating.product, 1)
//      for (a <- movies) {
//        z=z:+a.product
//        println(a + "-------" + a.product)
//      }
//    }
//    z.foreach(row=>println(row+"去重前"))
//    z.distinct.foreach(row=>println(row+"去重后"))
  }
}