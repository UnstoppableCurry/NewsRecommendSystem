package news.recommend.system.Util;

import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
 


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import news.recommend.system.contollor.RatingContollor;
import news.recommend.system.pojo.User;
 
public class mysqlPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
    	 Connection conn = JdbcUtil.getConnection();
    	 String id =  resultItems.get("movieID") ;             //文章题目
         String title = resultItems.get("fileName");             //文章题目
     	String context=resultItems.get("thedata");
         String sql = "INSERT INTO `deeplearning4scala`.`news_str` (`movieID`, `movieName`, `newsStr`) VALUES (?, ?, ?);";
      	String userid=resultItems.get("userId");
      	String lastid=resultItems.get("lastid");
    	Jedis jedis=new Jedis(RatingContollor.redisIP,6379);
    	jedis.lpush(userid,context);
    	jedis.lpush(lastid,context);//存贮这次的给下次用

         try {
             PreparedStatement ptmt = conn.prepareStatement(sql);
             ptmt.setString(1, id);;
             ptmt.setString(2,title);
             ptmt.setString(3,context);
             ptmt.execute();
         } catch (SQLException e) {
             e.printStackTrace();
         }
      
}}