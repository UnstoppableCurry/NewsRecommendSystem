package com.wtx.job014.movie

import scala.collection.SortedMap
import scala.collection.immutable.ListMap

object test {
  def main(args: Array[String]): Unit = {
//    var l = Array[String]()
//    var s = ""
//    ALSRecommendUser4GennerUtil.getArrayTheRecommendList4Genners("", Array(1, 2, 3, 20), 10).foreach(row => {
//      //      println(row.product)
//      l = l :+ row.product.toString()
//      s = row.product + ","
//    })
//
//    println("原长度为: " + l.length + "去重后长度为: " + l.distinct.length)
//    val map = Map[String, Int]()
//    val wordcount = generateMap(l.toList, map)
//    wordcount.foreach(row => println(row))
//    println("开始怕排序")
//    import scala.collection.immutable.ListMap //隐式转换
//    ListMap(wordcount.toSeq.sortBy(_._2): _*).foreach(row => println(row)) //降序
//    println("升序")
//    import scala.collection.immutable.ListMap //隐式转换
//    ListMap(wordcount.toSeq.sortWith( _._2 > _._2 ) :_ *).foreach(row => println(row)) //升序
//    ALSRecommendUser4GennerUtil.genner4moviesUtil("model/user_genner2_movies",Array(1, 2, 3, 20), 20).foreach(r=>println(r))
    var array= Array[Int]()
    args(1).split(",").foreach(row=>{
      array=array:+row.toInt
    })
      ALSRecommendUser4GennerUtil.genner4moviesUtil(args(0).toString(),array,args(2).toInt).foreach(r=>println(r))

  }
 
}