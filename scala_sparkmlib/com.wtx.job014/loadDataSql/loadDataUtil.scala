package com.wtx.job014.loadDataSql
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import org.apache.spark.SparkContext
import org.apache.spark.sql.{ Row, SaveMode }
import org.apache.spark.sql.{ DataFrame, SparkSession }
// 定义电影类别top10推荐对象
case class GenresRecommendation(genres: String, recs: Seq[Recommendation])
case class Recommendation(movieId: String, rating: Double)
case class GenresRecommendation2Mysql(genres: String, recs: String)
object loadData {
  /**
   * 离线推荐模块数据统计服务,统计后倒入数据库
   */
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
    /**
     * #统计每部电影有多少评分
     * select movieId, count(movieId) as count from ratings group by movieId
     * INSERT INTO RateMoreMovies(movieId,count) SELECT movieId, count(movieId) as count from ratings group by movieId
     *
     * select movieId, rating, changeDate(timestamp) as yearmonth from ratings LIMIT 20
     * #每部电影的平均评分
     * INSERT INTO AverageMovies(movieId,avg)
     * select movieId, avg(rating) as avg from ratings group by movieId
     */
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    //udf 日期格式化工具 电影热度排序
    spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt) //int类型最大值为2147483647,乘1000后超出上限所以要改为long类型
    val ratingOfMonthDF = spark.sql(" select movieId, rating, changeDate(timestamp) as yearmonth from ratings");
    ratingOfMonthDF.createOrReplaceTempView("ratingOfMonth")
    val RateMoreRecentlyMoviesDF = spark.sql("select movieId, count(movieId) as count ,yearmonth from ratingOfMonth group by yearmonth,movieId order by yearmonth desc,count desc")
    //热门新闻微博热搜榜单
    //RateMoreRecentlyMoviesDF.show(false)
    //电影平均分
    val averageMoviesDF = spark.sql("select movieId, avg(rating) as avg from ratings group by movieId")
    //各类别电影Top统计
    /**
     * 根据电影平均评分获取每个类别最热门电影推荐
     */
    val movieWithScore = moviesDF.join(averageMoviesDF, "movieId") //inner join 平均分表和电影表
    movieWithScore.show(false)
    //定义所有类别
    val genres = List("Action", "Animation", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
      "Drama", "Family", "Fantasy", "Foreign", "Film-Noir", "History", "Horror", "Music", "IMAX", "Mystery", "Romance", "Science", "Sci-Fi", "Tv", "Thriller", "War", "Western")
    val gennerRdd = spark.sparkContext.makeRDD(genres)
    //计算笛卡尔积
    import spark.implicits._ //将RDD转化成为DataFrame并支持SQL操作
    val genresTopMoviesDF = gennerRdd.cartesian(movieWithScore.rdd).filter {
      // 条件过滤，找出movie的字段genres值(Action|Adventure|Sci-Fi)包含当前类别genre(Action)的那些
      case (genre, movieRow) => movieRow.getAs[String]("genres").toLowerCase.contains(genre.toLowerCase)
    }
      .map {
        case (genre, movieRow) => (genre, (movieRow.getAs[String]("movieID"), movieRow.getAs[Double]("avg")))
      }
      .groupByKey()
      .map {
        case (genre, items) => GenresRecommendation(genre, items.toList.sortWith(_._2 > _._2).take(20).map(item => Recommendation(item._1, item._2)))
      }
    val thedata = genresTopMoviesDF.map(row => {
      var str = ""
      row.recs.foreach(rows => {
        val str2 = rows.movieId + "-" + rows.rating + ","
        str = str + str2
      })
      (row.genres, str)
    })
    thedata.foreach(println)
    save2MysqlAsDf(spark.createDataFrame(thedata).toDF("genners","topTenMovies"), "gennersRecommend_topTen_movies")
    /**
     * mysql不能存储非关系类型数据,所以要从mongodb的数据格式转到mysql的格式
     */
    //    var dataset: Map[String, String] = Map()
    //    var hashMap = new scala.collection.mutable.HashMap[String, String];
    //       var s = ""
    //    for (row <- genresTopMoviesDF) {
    //      println("每一个元素遍历:" + row.genres)
    //
    //      row.recs.foreach(rows => {
    //        val str = rows.movieId + "-" + rows.rating + "="
    //        s = s + str
    //      })
    ////      println(s)
    ////      hashMap.put(row.genres, s)
    //      hashMap +=(row.genres -> s)
    //      dataset += (row.genres.toString() -> s.toString())
    //      println("开始输出" + "1:" + dataset.size + " 2:" + hashMap.size)
    //      hashMap.foreach(println)
    //      dataset.foreach(println)
    ////      save2MysqlAsDf(hashMap.toSeq.toDF(), "genner_topten_recommend_movie")
    //    }
    //        println("最终输出" + "1:" + dataset.size + " 2:" + hashMap.size)
    //        hashMap.foreach(println)

    //    save2MysqlAsDf(spark.createDataFrame(genresTopMoviesDF), "gennersRecommend_topTen_movies")
    //    genresTopMoviesDF.foreach(println)
  }
  /**
   * 保存df到mysql方法,传入df和表名
   */
  def save2MysqlAsDf(df: DataFrame, tableName: String): Unit = {
    val prop = new java.util.Properties
    prop.setProperty("user", "root")
    prop.setProperty("password", "1f4e7e3217016745")
    df.write.mode(SaveMode.Append).jdbc("jdbc:mysql://192.168.1.9:3306/deeplearning4scala", tableName, prop)
  }
}