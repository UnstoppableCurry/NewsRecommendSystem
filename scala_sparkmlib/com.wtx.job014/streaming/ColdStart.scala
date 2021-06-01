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
object ColdStart {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession
      .builder()
      .appName("ALSDemo")
      .master("local[*]").
      getOrCreate()
    import spark.implicits._ //隐式转换直接转换df类型,不然会报错
    val moviesFile = "file:\\C:\\Users\\86183\\Desktop\\scala_machine_leraning_projects\\ScalaMachineLearningData\\电影推荐\\16w用户对6w电影评价训练集ml-25m\\ml-25m\\movies.csv"
    val df2 = spark.read.format("com.databricks.spark.csv").option("header", true).load(moviesFile)
    val moviesDF = df2.select(df2.col("movieID"), df2.col("title"), df2.col("genres"))
    val dataframes = moviesDF.rdd.map(row => {
      val movieID = row.getString(0).toInt
      val title = row.getString(1)
      val genres = row.getString(2).replace("|", " ")
      (movieID, title, genres)
    })
    spark.createDataFrame(dataframes).show(false) //将genners 的|用空格替换
    //    val dataframes = df2.toDF("movieID", "title", "genres").map(row => (row.getAs[Int]("movieID"), row.getAs[String]("title"), row.getAs[String]("genres").map(c => if (c == '|') ' ' else c)))
    // 创建分词器,按照空格分词
//    val tokenizer = new Tokenizer().setInputCol("genres").setOutputCol("words")
//    // 分词后添加新的一列
//    val wordsData = tokenizer.transform(spark.createDataFrame(dataframes).toDF("movieID", "title", "genres"))
//    //计算词频,和逆文档词频
//    wordsData.show(false)
//    val hashTF = new HashingTF().setInputCol("words").setOutputCol("rowfeature").setNumFeatures(50)
//    //默认哈希桶值为2的16次方 26w个分区桶,是为了防止hash冲突用的,但是这里用不到,因为genners标签的数量只有20个
//    val rowfeature = hashTF.transform(wordsData) //转置添加一列
//    rowfeature.show(false)
//    //引入idf工具
//    val idf = new IDF().setInputCol("rowfeature").setOutputCol("feature")
//    //训练idf工具获取逆文档词频
//    val idfModel = idf.fit(rowfeature) //获取每个词的词频
//    //    idfModel.save("model/IDF-TF-Model") //保存逆文档模型
//    val resultData = idfModel.transform(rowfeature) //将词频添加进df
//    resultData.show(false)
//    //计算笛卡尔积
//    val movieFeatures = resultData.map(row =>
//      (row.getAs[Int]("movieID"), row.getAs[SparseVector]("feature").toArray)).rdd.map(
//      x => (x._1, new DoubleMatrix(x._2))) //转化成需要的向量数据类型
//    movieFeatures.collect().foreach(println)
//    //计算标签两两相似度
//    var i = 0 //计次
//     var needtime=0l
//    val starttime = System.currentTimeMillis()
//    val movieRecs = movieFeatures.cartesian(movieFeatures).filter {
//      // 把自己跟自己的配对过滤掉
//      case (a, b) => a._1 != b._1
//    }
//      .map {
//
//        case (a, b) => {
//          i = i + 1
//          println("相似度计算次数: " + i + "   整体进度" + (i / 30000000.00000) + "%" )
//          val simScore = this.consinSim(a._2, b._2) //计算相似度
//          (a._1, (b._1, simScore))
//        }
//      }
//      .filter(_._2._2 > 0.98) // 过滤出相似度大于0.98的
//      .groupByKey()
//      .map {
//        case (mid, items) => MovieRecs(mid, items.toList.sortWith(_._2 > _._2).map(x => Recommendation(x._1, x._2)))
//      }
//    //      .toDF()
//    println("矩阵计算完成导入数据")
//    var l = 1; //计次
//    val sqlPojo = movieRecs.map(r => {
//      l = l + 1
//      println("矩阵最后处理次数: " + l + "   整体进度" + (i / 30000L) + "%")
//      var str = ""
//      r.recs.foreach(rows => {
//        str = str + rows.mid.toString() + "-" + rows.score.toString() + ","
//      })
//      MovieRecsFifter(r.mid, str)
//    }).toDF("movied", "recommend_collections")
//    loadData.save2MysqlAsDf(sqlPojo, "Tf_IDF_Recommend_movie_likes")
//     sqlPojo.coalesce(1).write.option("header", "true").csv("/data/recommend_Demo.csv")

  }
  def consinSim(movie1: DoubleMatrix, movie2: DoubleMatrix): Double = {
    movie1.dot(movie2) / (movie1.norm2() * movie2.norm2())
  }
}

