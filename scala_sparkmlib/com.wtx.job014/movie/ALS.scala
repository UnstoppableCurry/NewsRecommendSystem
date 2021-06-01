package com.wtx.job014.movie
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating
import scala.Tuple2
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.network.util.IOMode

object ALS {
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

    ratingDF.createOrReplaceTempView("ratings")
    moviesDF.createOrReplaceTempView("movies")

    val numRatings = selectedRatingsDF.count()
    val numUsers = selectedRatingsDF.select(selectedRatingsDF.col("userId")).distinct().count()
    //distinct:mysql去除重复
    val numMoveis = selectedRatingsDF.select(selectedRatingsDF.col("movieId")).distinct().count()
    print("numRatings:" + numRatings + " numUsers:" + numUsers + " numMoveis:" + numMoveis)
    //numRatings:25000095 numUsers:162541 numMoveis59047
    // Get the max, min ratings along with the count of users who have rated a movie.
    val results = spark.sql("select * from movies where movieId=149268");

    results.show(false)

    // Show the top 10 most-active users and how many times they rated a movie
    val mostActiveUsersSchemaRDD = spark.sql("SELECT ratings.userId, count(*) as ct from ratings "
      + "group by ratings.userId order by ct desc limit 10")
    mostActiveUsersSchemaRDD.show(false)
    /**
     * numRatings:25000095 numUsers:162541 numMoveis:59047+------------------------------------------------------------------------------+----+----+-----+
     * |title                                                                         |maxr|minr|cntu |
     * +------------------------------------------------------------------------------+----+----+-----+
     * |Forrest Gump (1994)                                                           |5.0 |0.5 |81491|
     * |Shawshank Redemption, The (1994)                                              |5.0 |0.5 |81482|
     * |Pulp Fiction (1994)                                                           |5.0 |0.5 |79672|
     * |Silence of the Lambs, The (1991)                                              |5.0 |0.5 |74127|
     * |Matrix, The (1999)                                                            |5.0 |0.5 |72674|
     * |Star Wars: Episode IV - A New Hope (1977)                                     |5.0 |0.5 |68717|
     * |Jurassic Park (1993)                                                          |5.0 |0.5 |64144|
     * |Schindler's List (1993)                                                       |5.0 |0.5 |60411|
     * |Braveheart (1995)                                                             |5.0 |0.5 |59184|
     * |Fight Club (1999)                                                             |5.0 |0.5 |58773|
     * |Terminator 2: Judgment Day (1991)                                             |5.0 |0.5 |57379|
     * |Star Wars: Episode V - The Empire Strikes Back (1980)                         |5.0 |0.5 |57361|
     * |Toy Story (1995)                                                              |5.0 |0.5 |57309|
     * |Lord of the Rings: The Fellowship of the Ring, The (2001)                     |5.0 |0.5 |55736|
     * |Usual Suspects, The (1995)                                                    |5.0 |0.5 |55366|
     * |Star Wars: Episode VI - Return of the Jedi (1983)                             |5.0 |0.5 |54917|
     * |Raiders of the Lost Ark (Indiana Jones and the Raiders of the Lost Ark) (1981)|5.0 |0.5 |54675|
     * |American Beauty (1999)                                                        |5.0 |0.5 |53689|
     * |Godfather, The (1972)                                                         |5.0 |0.5 |52498|
     * |Lord of the Rings: The Two Towers, The (2002)                                 |5.0 |0.5 |51138|
     * +------------------------------------------------------------------------------+----+----+-----+
     * only showing top 20 rows
     *
     * +------+-----+
     * |userId|ct   |
     * +------+-----+
     * |72315 |32202|
     * |80974 |9178 |
     * |137293|8913 |
     * |33844 |7919 |
     * |20055 |7488 |
     * |109731|6647 |
     * |92046 |6564 |
     * |49403 |6553 |
     * |30879 |5693 |
     * |115102|5649 |
     * +------+-----+
     */
    // Find the movies that user 72315 rated higher than 4
    val results2 = spark.sql(
      "SELECT ratings.userId, ratings.movieId,"
        + "ratings.rating, movies.title FROM ratings JOIN movies "
        + "ON movies.movieId=ratings.movieId "
        + "where ratings.userId=72315 and ratings.rating > 4")
    /**
     * +------+-------+------+-------------------------------------------------+
     * |userId|movieId|rating|title                                            |
     * +------+-------+------+-------------------------------------------------+
     * |72315 |97     |5.0   |Hate (Haine, La) (1995)                          |
     * |72315 |136    |5.0   |From the Journals of Jean Seberg (1995)          |
     * |72315 |215    |5.0   |Before Sunrise (1995)                            |
     * |72315 |306    |5.0   |Three Colors: Red (Trois couleurs: Rouge) (1994) |
     * |72315 |307    |5.0   |Three Colors: Blue (Trois couleurs: Bleu) (1993) |
     * |72315 |529    |5.0   |Searching for Bobby Fischer (1993)               |
     * |72315 |530    |5.0   |Second Best (1994)                               |
     * |72315 |670    |5.0   |World of Apu, The (Apur Sansar) (1959)           |
     * |72315 |723    |5.0   |Two Friends (1986)                               |
     * |72315 |746    |5.0   |Force of Evil (1948)                             |
     * |72315 |756    |5.0   |Carmen Miranda: Bananas Is My Business (1994)    |
     * |72315 |854    |5.0   |Ballad of Narayama, The (Narayama Bushiko) (1958)|
     * |72315 |896    |5.0   |Wild Reeds (Les roseaux sauvages) (1994)         |
     * |72315 |907    |5.0   |Gay Divorcee, The (1934)                         |
     * |72315 |913    |5.0   |Maltese Falcon, The (1941)                       |
     * |72315 |923    |5.0   |Citizen Kane (1941)                              |
     * |72315 |926    |5.0   |All About Eve (1950)                             |
     * |72315 |942    |5.0   |Laura (1944)                                     |
     * |72315 |944    |5.0   |Lost Horizon (1937)                              |
     * |72315 |1131   |5.0   |Jean de Florette (1986)                          |
     * +------+-------+------+-------------------------------------------------+
     * only showing top 20 rows
     */
    results2.show(false)

    val splits = selectedRatingsDF.randomSplit(Array(0.75, 0.25), seed = 12345L)
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
    //    val startTime=System.currentTimeMillis()
    //        val model=new ALS().setIterations(numIterations).setBlocks(block).setAlpha(alpha).setLambda(lambda).setRank(rank).setSeed(seed).setImplicitPrefs(implicitPrefs).run(ratingsRDD)
    //        val savedALSModel=model.save(spark.sparkContext, "model/MovieRecomModel")
    //        val endTime=System.currentTimeMillis()
    //        println("训练模型完毕,用时"+(endTime-startTime)/1000)
    //        val topN=model.recommendProducts(72315, 10)
    //        for(rating <- topN){
    //          println(rating.toString())
    //        }
    //    println("----结束----")
    /**
     * 训练模型完毕,用时52
     * Rating(72315,173871,4.861750235947504) I'll Take You There (1999)
     * Rating(72315,101862,4.848760605703874)50 Children: The Rescue Mission of Mr. And Mrs. Kraus (2013)|Documentary
     * Rating(72315,123415,4.838048214302841)Poster Girl (2010)|(no genres listed)|
     * Rating(72315,204488,4.838048214302841)The Lurking Man (2018)|Drama|Fantasy|Horror|
     * Rating(72315,164624,4.838048214302841)The Girl from Flanders (1956)|(no genres listed)|
     * Rating(72315,167340,4.838048214302841)Campaign of Hate: Russia and Gay Propaganda (2014)|(no genres listed)|
     * Rating(72315,149268,4.838048214302841)The Pied Piper (1942)|(no genres listed)|
     *
     *
     * * Rating(72315,173871,4.861750235947504) I'll Take You There (1999)
     * Rating(72315,101862,4.848760605703874)50 Children: The Rescue Mission of Mr. And Mrs. Kraus (2013)|Documentary
     * Rating(72315,123415,4.838048214302841)Poster Girl (2010)|(no genres listed)|
     * Rating(72315,204488,4.838048214302841)The Lurking Man (2018)|Drama|Fantasy|Horror|
     * Rating(72315,164624,4.838048214302841)The Girl from Flanders (1956)|(no genres listed)|
     * Rating(72315,167340,4.838048214302841)Campaign of Hate: Russia and Gay Propaganda (2014)|(no genres listed)|
     * Rating(72315,149268,4.838048214302841)The Pied Piper (1942)|(no genres listed)|
     * Rating(72315,173871,4.861750235947504) I'll Take You There (1999)
     * Rating(72315,101862,4.848760605703874)50 Children: The Rescue Mission of Mr. And Mrs. Kraus (2013)|Documentary
     * Rating(72315,123415,4.838048214302841)Poster Girl (2010)|(no genres listed)|
     * Rating(72315,204488,4.838048214302841)The Lurking Man (2018)|Drama|Fantasy|Horror|
     * Rating(72315,164624,4.838048214302841)The Girl from Flanders (1956)|(no genres listed)|
     * Rating(72315,167340,4.838048214302841)Campaign of Hate: Russia and Gay Propaganda (2014)|(no genres listed)|
     * Rating(72315,149268,4.838048214302841)The Pied Piper (1942)|(no genres listed)|
     * 评级（72315173871,4.861750235947504）我带你去那里（1999）
     *
     * 评级（72315101862,4.848760605703874）50名儿童：Kraus先生和夫人的救援任务（2013）|纪录片
     *
     * 评级（72315123415,4.838048214302841）海报女郎（2010）|（未列出流派）|
     *
     * 评级（72315204488,4.838048214302841）《潜伏者》（2018）|戏剧|幻想|恐怖|
     *
     * 评分（72315164624,4.838048214302841）来自佛兰德斯的女孩（1956）|（未列出流派）|
     *
     * 评级（72315167340,4.838048214302841）仇恨运动：俄罗斯和同性恋宣传（2014）|（未列出流派）|
     *
     * 评级（72315149268,4.838048214302841）Pied Piper（1942）|（未列出任何类型）
     */

    val io_model = MatrixFactorizationModel.load(spark.sparkContext, "model/MovieRecomModel")
    val rmseTest = computeRmse(io_model, testRDD, true)
    println("testRMSE:  	=>   " + rmseTest)
    /**
     * (3.8255829030603925,3.5)
     * (3.466203956095514,3.0)
     * (3.433352733128467,3.0)
     * (3.4378208067278955,3.5)
     * (4.090141194525656,4.0)
     * testRMSE:  	=>   0.8022676728294124
     */
  }
}