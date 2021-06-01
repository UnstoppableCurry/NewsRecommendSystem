package com.wtx.job014.loadDataSql

import scala.collection.mutable.ListBuffer


object pazzle {
 
  def main(args:Array[String]): Unit =
  {
    println(pazzle.getpailie_num(4,2,0))
 
    println(pazzle.getzuhe_num(3,2))
    var l = new ListBuffer[String] 
    for(i<-2 to 20){
    var arrayTest2 = Array("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20")
    var selected = new Array[String](i)
    var str=pazzle.print_zuhe(arrayTest2,selected,l,i,0 )
    }
    println(l.size)
    l.foreach(row=>{
//      println(row)
      var arr= List[Int]()
      row.split(",").foreach(str =>{
        arr=arr :+ str.toInt
      } )
      println("去重前: "+arr.length)
      println("去重后"+arr.distinct.length)
    })
  }

 
//获取组合数
 
  def getzuhe_num(n:Int,m:Int):Int={
 
    getpailie_num(n,m,0)/getpailie_num(m,m,0)
 
  }
  def swap(data:Array[String],i:Int,j:Int): Unit =
  {
    var temp = data(i)
    data(i)=data(j)
    data(j)=temp
  }
 
//输出所有组合可能
  def print_zuhe(data:Array[String],selected:Array[String],arr: ListBuffer[String],n:Int,index:Int): Unit ={
 
    if(index>=n)
      {
      var str=""
        selected.foreach(item => {
//        print(item + ",")
        str = str+item.toString()+","
        })
        arr +=str
        println()
//        return  selected
      }
    else
      {
 
        for(i <- index to data.length-(n-index)){
            selected(index)=data(i)
            //
            print_zuhe(data,selected,arr,n,i+1)
          }
      }
 
  }
 
 
  //全排列输出
  def print_pailie(data:Array[String],index:Int): Unit ={
 
    if(index>=data.length-1)
      {
        data.map(item => print(item))
        println("----")
      }
    else {
 
      for(i <- index  until  data.length){
          swap(data,index,i)
          print_pailie(data,index+1)
          swap(data,index,i)
 
        }
 
    }
  }
 
  //获取排列数
  def getpailie_num(n:Int,m:Int,index:Int):Int={
    /*
    n=4 m =2
    num = 4*3 = 12
    n=4 m =4
    num = 4*3*2*1
    n=4 m=3
    num = 4*3*2
    n*(n-1)*(n-2),
     */
    if(index==m)
      1
    else
    //index < m
    (n-index)*getpailie_num(n,m,index+1)
 
 
  }
 
 
}
 
  
