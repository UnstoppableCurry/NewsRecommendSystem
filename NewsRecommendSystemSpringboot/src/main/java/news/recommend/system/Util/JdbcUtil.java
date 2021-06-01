package news.recommend.system.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class JdbcUtil {
 
    private static final String URL="jdbc:mysql://192.168.1.8:3306/deeplearning4scala";
    private static final String USER="root";
    private static final String PASSWORD="1f4e7e3217016745";
    private static Connection conn = null;
    static {
        try{
            //1.加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获得数据库的连接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){
        return conn;
    }
}