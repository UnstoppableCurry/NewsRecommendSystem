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
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.catalyst.expressions.Concat
import scala.collection.immutable.ListMap
/**
 *   als 用户推荐核心算法,废弃,需要用TF-IDF计算(逆文本词频统计),基于物品推荐Item CF需要对标签进行稀疏矩阵处理,不能行转列将特征值下降而该膨胀特征值
 * 	 排列组合问题中C20 n(20个标签,任意中标签组合进行推荐),大概有50万种组合,每种组合的离线处理需要大量计算量,并不现实
 * 
 */
object ALSRecommendUser4GennerUtil {
  /**
   * 根据genner推荐电影id
   */
  def getTheRecommendList4Genners(path: String, gennerId: Int, TopNumber: Int): Array[Rating] = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
    val user_Reconmend_model = MatrixFactorizationModel.load(spark.sparkContext, "model/user_genner2_movies") //"model/user_genner"
    val topN = user_Reconmend_model.recommendProducts(gennerId, TopNumber)
    //    val rmseTest = computeRmse(user_Reconmend_model, testRDD, true)
    //    println("testRMSE:  	=>   " + rmseTest)
    return topN.distinct
  }

    def getArrayTheRecommendList4Genners(path: String, gennerIdArray: Array[Int], TopNumber: Int): Array[Rating] = {
       Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
      val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
    val user_Reconmend_model = MatrixFactorizationModel.load(spark.sparkContext, path) //"model/user_genner"
    var z:Array[Int] = new Array[Int](50)
    var l= Array[Rating]()
    var i:Int=0
    for(a<-gennerIdArray){
            user_Reconmend_model.recommendProducts(a, TopNumber).foreach(row=>{
              l=l:+row
            })
    }
    return l.distinct
  }
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
    id_gennerDF.show(false)
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
    val finalTable = spark.sql("select ID,movieId,rating from final")
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
        val movie = row.getString(1)
        val rating = row.getString(2)
        Rating(ID.toInt, movie.toInt, rating.toDouble)
      })
    val testRDD = testData.rdd.map(row => {
      val ID = row.getString(0)
      val movie = row.getString(1)
      val rating = row.getString(2)
      Rating(ID.toInt, movie.toInt, rating.toDouble)
    })
    //至此als所需rdd数据集已经准备好
    val rank = 20 //学习次数
    val numIterations = 15
    val lambda = 0.10
    val alpha = 1.00
    val block = -1
    val seed = 12345L
    val implicitPrefs = false
    //    als超参设置
    //        val startTime = System.currentTimeMillis()
    //        val model = new ALS().setIterations(numIterations).setBlocks(block).setAlpha(alpha).setLambda(lambda).setRank(rank).setSeed(seed).setImplicitPrefs(implicitPrefs).run(ratingsRDD)
    //        val savedALSModel = model.save(spark.sparkContext, "model/user_genner2_movies")
    //        val endTime = System.currentTimeMillis()
    //        println("训练模型完毕,用时" + (endTime - startTime) / 1000)
    //        val topN = model.recommendProducts(8, 10)
    //        for (rating <- topN) {
    //          println(rating.toString())
    //        }
    //        println("----1结束----")

    val io_model = MatrixFactorizationModel.load(spark.sparkContext, "model/user_genner2_movies")
    val topN = io_model.recommendProducts(12, 10)
    for (rating <- topN) {
      println(rating.toString())
    }
    /**
     * Rating(8,179685,4.934345056385428)
     * Rating(8,183789,4.934345056385428)
     * Rating(8,184669,4.934345056385428)
     * Rating(8,139453,4.934345056385428)
     * Rating(8,185813,4.934345056385428)
     * Rating(8,139773,4.934345056385428)
     * Rating(8,188573,4.934345056385428)
     * Rating(8,182589,4.934345056385428)
     * Rating(8,195549,4.934345056385428)
     * Rating(8,137733,4.934345056385428)
     */
    println("----1结束----")

    //    val topN2 = io_model.recommendProductsForUsers(10)
    //    for (rating <- topN2) {
    //      println(rating._1)
    //      rating._2.foreach(row=>println(row+"***"))
    //    }
    //    println("----1结束----")
    /**
     * Rating(6,182589,4.908446688387359)***
     * Rating(4,182589,4.913487332052968)***
     * Rating(1,182589,4.9131731525711935)***
     * Rating(4,185813,4.913487332052968)***
     * Rating(1,183789,4.9131731525711935)***
     * Rating(4,196621,4.913487332052968)***
     * Rating(1,185813,4.9131731525711935)***
     * Rating(4,183789,4.913487332052968)***
     * Rating(1,195549,4.9131731525711935)***
     * Rating(6,185813,4.908446688387359)***
     * Rating(1,196621,4.9131731525711935)***
     * Rating(6,196621,4.908446688387359)***
     * Rating(1,137733,4.9131731525711935)***
     * Rating(4,139773,4.913487332052968)***
     * Rating(1,184669,4.9131731525711935)***
     * Rating(6,183789,4.908446688387359)***
     * Rating(1,139453,4.9131731525711935)***
     * Rating(4,137733,4.913487332052968)***
     * Rating(1,139773,4.9131731525711935)***
     * Rating(4,184669,4.913487332052968)***
     * Rating(4,139453,4.913487332052968)***
     * Rating(6,139773,4.908446688387359)***
     * Rating(4,195549,4.913487332052968)***
     * Rating(6,137733,4.908446688387359)***
     * Rating(1,188573,4.9131731525711935)***
     * Rating(4,188573,4.913487332052968)***
     * Rating(6,184669,4.908446688387359)***
     * Rating(6,139453,4.908446688387359)***
     * Rating(6,195549,4.908446688387359)***
     * Rating(6,188573,4.908446688387359)***
     * 8
     * Rating(8,182589,4.934345056385428)***
     * Rating(8,185813,4.934345056385428)***
     * Rating(8,196621,4.934345056385428)***
     * 7
     * Rating(7,207095,4.963031408572654)***
     * Rating(7,164278,4.9629797556417214)***
     * Rating(7,144208,4.962185698504335)***
     * Rating(7,92783,4.961187048028084)***
     * Rating(8,183789,4.934345056385428)***
     * Rating(8,139773,4.934345056385428)***
     * Rating(8,137733,4.934345056385428)***
     * Rating(8,184669,4.934345056385428)***
     * Rating(8,139453,4.934345056385428)***
     * Rating(8,195549,4.934345056385428)***
     * Rating(8,188573,4.934345056385428)***
     * Rating(7,191973,4.957908648871621)***
     * Rating(7,208090,4.957908648871621)***
     * Rating(7,189189,4.87702253258387)***
     * Rating(7,158165,4.87702253258387)***
     * Rating(7,184541,4.87702253258387)***
     * Rating(7,184821,4.87702253258387)***
     * 2
     * Rating(2,182589,4.90873201536756)***
     * Rating(2,185813,4.90873201536756)***
     * Rating(2,196621,4.90873201536756)***
     * Rating(2,183789,4.90873201536756)***
     * Rating(2,139773,4.90873201536756)***
     * Rating(2,137733,4.90873201536756)***
     * Rating(2,184669,4.90873201536756)***
     * Rating(2,139453,4.90873201536756)***
     * Rating(2,195549,4.90873201536756)***
     * Rating(2,188573,4.90873201536756)***
     * 11
     * Rating(11,151373,4.876337944618571)***
     * Rating(11,166293,4.876337944618571)***
     * Rating(11,159141,4.876337944618571)***
     * Rating(11,160541,4.876337944618571)***
     * Rating(11,174101,4.876337944618571)***
     * Rating(11,159125,4.876337944618571)***
     * Rating(11,133341,4.876337944618571)***
     * Rating(11,139565,4.876337944618571)***
     * Rating(11,137805,4.876337944618571)***
     * Rating(11,151517,4.876337944618571)***
     * 9
     * Rating(9,182589,4.942607015641631)***
     * Rating(9,183789,4.942607015641631)***
     * Rating(9,185813,4.942607015641631)***
     * Rating(9,195549,4.942607015641631)***
     * Rating(9,196621,4.942607015641631)***
     * Rating(9,137733,4.942607015641631)***
     * Rating(9,184669,4.942607015641631)***
     * Rating(9,139453,4.942607015641631)***
     * Rating(9,139773,4.942607015641631)***
     * Rating(9,188573,4.942607015641631)***
     * 10
     * Rating(10,182589,4.913442390150691)***
     * Rating(10,185813,4.913442390150691)***
     * Rating(10,196621,4.913442390150691)***
     * Rating(10,183789,4.913442390150691)***
     * Rating(10,139773,4.913442390150691)***
     * Rating(10,137733,4.913442390150691)***
     * Rating(10,184669,4.913442390150691)***
     * Rating(10,139453,4.913442390150691)***
     * Rating(10,195549,4.913442390150691)***
     * Rating(10,188573,4.913442390150691)***
     * 16
     * 15
     * Rating(16,182589,4.902142892009357)***
     * Rating(15,182589,4.91181671686642)***
     * Rating(16,185813,4.902142892009357)***
     * Rating(15,185813,4.91181671686642)***
     * Rating(16,196621,4.902142892009357)***
     * Rating(16,183789,4.902142892009357)***
     * Rating(16,139773,4.902142892009357)***
     * Rating(16,137733,4.902142892009357)***
     * Rating(16,184669,4.902142892009357)***
     * Rating(16,139453,4.902142892009357)***
     * Rating(16,195549,4.902142892009357)***
     * Rating(16,188573,4.902142892009357)***
     * Rating(15,196621,4.91181671686642)***
     * Rating(15,183789,4.91181671686642)***
     * Rating(15,139773,4.91181671686642)***
     * Rating(15,137733,4.91181671686642)***
     * Rating(15,184669,4.91181671686642)***
     * Rating(15,139453,4.91181671686642)***
     * Rating(15,195549,4.91181671686642)***
     * Rating(15,188573,4.91181671686642)***
     * 12
     * Rating(12,182589,4.888196350898288)***
     * Rating(12,183789,4.888196350898288)***
     * Rating(12,185813,4.888196350898288)***
     * Rating(12,195549,4.888196350898288)***
     * Rating(12,196621,4.888196350898288)***
     * Rating(12,137733,4.888196350898288)***
     * Rating(12,184669,4.888196350898288)***
     * Rating(12,139453,4.888196350898288)***
     * Rating(12,139773,4.888196350898288)***
     * Rating(12,188573,4.888196350898288)***
     * 17
     * Rating(17,182589,4.918251627482325)***
     * Rating(17,183789,4.918251627482325)***
     * Rating(17,185813,4.918251627482325)***
     * Rating(17,195549,4.918251627482325)***
     * Rating(17,196621,4.918251627482325)***
     * Rating(17,137733,4.918251627482325)***
     * Rating(17,184669,4.918251627482325)***
     * Rating(17,139453,4.918251627482325)***
     * Rating(17,139773,4.918251627482325)***
     * Rating(17,188573,4.918251627482325)***
     * 5
     * Rating(5,182589,4.925560336880415)***
     * Rating(5,185813,4.925560336880415)***
     * Rating(5,196621,4.925560336880415)***
     * Rating(5,183789,4.925560336880415)***
     * Rating(5,139773,4.925560336880415)***
     * Rating(5,137733,4.925560336880415)***
     * Rating(5,184669,4.925560336880415)***
     * Rating(5,139453,4.925560336880415)***
     * Rating(5,195549,4.925560336880415)***
     * Rating(5,188573,4.925560336880415)***
     * 18
     * Rating(18,182589,4.9027615159556)***
     * Rating(18,185813,4.9027615159556)***
     * Rating(18,196621,4.9027615159556)***
     * Rating(18,183789,4.9027615159556)***
     * Rating(18,139773,4.9027615159556)***
     * Rating(18,137733,4.9027615159556)***
     * Rating(18,184669,4.9027615159556)***
     * Rating(18,139453,4.9027615159556)***
     * Rating(18,195549,4.9027615159556)***
     * Rating(18,188573,4.9027615159556)***
     * 13
     * Rating(13,182589,4.9157281801912465)***
     * Rating(13,185813,4.9157281801912465)***
     * Rating(13,196621,4.9157281801912465)***
     * Rating(13,183789,4.9157281801912465)***
     * Rating(13,139773,4.9157281801912465)***
     * Rating(13,137733,4.9157281801912465)***
     * Rating(13,184669,4.9157281801912465)***
     * Rating(13,139453,4.9157281801912465)***
     * Rating(13,195549,4.9157281801912465)***
     * Rating(13,188573,4.9157281801912465)***
     * 20
     * 19
     * Rating(19,182589,4.906810033200118)***
     * Rating(19,183789,4.906810033200118)***
     * Rating(19,185813,4.906810033200118)***
     * Rating(19,195549,4.906810033200118)***
     * Rating(19,196621,4.906810033200118)***
     * Rating(19,137733,4.906810033200118)***
     * Rating(19,184669,4.906810033200118)***
     * Rating(19,139453,4.906810033200118)***
     * Rating(19,139773,4.906810033200118)***
     * Rating(19,188573,4.906810033200118)***
     * Rating(20,182589,4.905871783990142)***
     * Rating(20,183789,4.905871783990142)***
     * Rating(20,185813,4.905871783990142)***
     * Rating(20,195549,4.905871783990142)***
     * Rating(20,196621,4.905871783990142)***
     * Rating(20,137733,4.905871783990142)***
     * Rating(20,184669,4.905871783990142)***
     * Rating(20,139453,4.905871783990142)***
     * Rating(20,139773,4.905871783990142)***
     * Rating(20,188573,4.905871783990142)***
     * 3
     * Rating(3,182589,4.926164630426923)***
     * Rating(3,183789,4.926164630426923)***
     * Rating(3,185813,4.926164630426923)***
     * Rating(3,195549,4.926164630426923)***
     * Rating(3,196621,4.926164630426923)***
     * Rating(3,137733,4.926164630426923)***
     * Rating(3,184669,4.926164630426923)***
     * Rating(3,139453,4.926164630426923)***
     * Rating(3,139773,4.926164630426923)***
     * Rating(3,188573,4.926164630426923)***
     * 14
     * Rating(14,182589,4.924352668572112)***
     * Rating(14,183789,4.924352668572112)***
     * Rating(14,185813,4.924352668572112)***
     * Rating(14,195549,4.924352668572112)***
     * Rating(14,196621,4.924352668572112)***
     * Rating(14,137733,4.924352668572112)***
     * Rating(14,184669,4.924352668572112)***
     * Rating(14,139453,4.924352668572112)***
     * Rating(14,139773,4.924352668572112)***
     * Rating(14,188573,4.924352668572112)***
     */
//    val rmseTest = computeRmse(io_model, testRDD, true)
//    println("testRMSE:  	=>   " + rmseTest)
    /**
     * (Prediction, Rating)
     * (3.6925826634310313,2.0)
     * (3.6925826634310313,4.0)
     * (3.6925826634310313,4.0)
     * (3.6925826634310313,5.0)
     * (3.6925826634310313,3.0)
     * testRMSE:  	=>   0.9395091997063927
     */
  }
  def genner4moviesUtil(path:String,array:Array[Int],RecommendNmuber:Int):ListMap[String,Int]={
    
    var l = Array[String]()
    ALSRecommendUser4GennerUtil.getArrayTheRecommendList4Genners(path, array, RecommendNmuber).foreach(row => {
      //      println(row.product)
      l = l :+ row.product.toString()
    })

    println("原长度为: " + l.length + "去重后长度为: " + l.distinct.length)
    val map = Map[String, Int]()
    val wordcount = generateMap(l.toList, map)
//    wordcount.foreach(row => println(row))
//    println("开始怕排序")
//    println("升序")
    import scala.collection.immutable.ListMap //隐式转换
    val recommendData=ListMap(wordcount.toSeq.sortWith( _._2 > _._2 ) :_ *)
    if (recommendData.size<RecommendNmuber) {
      return recommendData
    }else if (recommendData.size>=RecommendNmuber) {
      return recommendData.take(RecommendNmuber)
    }else {
      return recommendData
    }
  }
  /**
   * worldcount词频统计
   */
  @scala.annotation.tailrec
  def generateMap(list: List[String], map: Map[String, Int]): Map[String, Int] = list match {
    case x :: y => if (map.keySet.contains(x)) generateMap(y, map ++ Map(x -> (map(x) + 1))) else generateMap(y, map ++ Map(x -> 1))
    case Nil    => map
  }
}