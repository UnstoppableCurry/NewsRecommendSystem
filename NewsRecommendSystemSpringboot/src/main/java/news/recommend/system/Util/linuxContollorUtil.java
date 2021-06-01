package news.recommend.system.Util;

import java.io.InputStream;


import java.util.ArrayList;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class linuxContollorUtil {
	 private Session session;
	    // 其他方法实现放别的章节分开展示
 /**
  * 远程登录
  */
 public void login(String host, int port, String user, String password) {
     try {
         JSch jsch = new JSch();
         session = jsch.getSession(user, host, port);
         session.setPassword(password);
         // 设置第一次登陆的时候提示，可选值:(ask | yes | no)
         session.setConfig("StrictHostKeyChecking", "no");
         // 连接超时
         session.connect(1000 * 10);

     } catch (JSchException e) {
         System.out.println("登录时发生错误！");
         e.printStackTrace();
     }
 }

 /**
  * 关闭连接
  */
 public void close() {
     if (session.isConnected())
         session.disconnect();
 }
 /**
  * 执行命令
  *
  * @param command 
  * @return
  * @throws Exception
  */
 public String executeShell(String command) throws Exception {

     byte[] tmp = new byte[1024];
     StringBuffer resultBuffer = new StringBuffer(); // 命令返回的结果

     Channel channel = session.openChannel("exec");
     ChannelExec exec = (ChannelExec) channel;
     // 返回结果流（命令执行错误的信息通过getErrStream获取）
     InputStream stdStream = exec.getInputStream();

     exec.setCommand(command);
     exec.connect();

     try {
         // 开始获得SSH命令的结果
         while (true) {
             while (stdStream.available() > 0) {
                 int i = stdStream.read(tmp, 0, 1024);
                 if (i < 0) break;
                 resultBuffer.append(new String(tmp, 0, i));
             }
             if (exec.isClosed()) {
//					System.out.println(resultBuffer.toString());
                 break;
             }
             try {
                 Thread.sleep(200);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     } finally {
         //关闭连接
         channel.disconnect();
     }
     return resultBuffer.toString();
 }
 public static java.util.List<Integer> getALSRecommendsStrUtil(String host,String shellOrder){
 	java.util.List<Integer> list=new ArrayList<Integer>();
     int port = 22;
     String user = "root";
     String password = "awangtianxin.666";
     linuxContollorUtil obj=new linuxContollorUtil();
     obj.login(host, port, user, password);
 	String str="";
    try {
    str= obj.executeShell(shellOrder).replace("Rating", "");
			System.out.println(str);
			int i=str.indexOf("(");
			String fs=str.substring(i,str.length()-1);
			System.out.println(fs);
			String[] arr=fs.trim().split("\\)");
			for (String string : arr) {
				String num=string.split(",")[1].replace("(", "").trim();
				System.out.println(num);
				list.add(Integer.parseInt(num));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
	        obj.close();
		}
		return list;
 }
/**
 * 
 	java.util.List<Integer> list=new ArrayList<Integer>();
 	 String host = "192.168.0.114";
      int port = 22;
      String user = "root";
      String password = "awangtianxin.666";
      linuxContollorUtil obj=new linuxContollorUtil();
      obj.login(host, port, user, password);
     try {
     	String str= obj.executeShell("cd /root/deeplearning/genners  && java -cp deeplearning-assembly-0.1.0-SNAPSHOT.jar  com.wtx.job014.movie.test /root/deeplearning/model/recommendMovies4User 1 10");
			System.out.println(str);
			int i=str.indexOf("(");
			String fs=str.substring(i,str.length()-1);
			System.out.println(fs);
			String[] arr=fs.trim().split("\\)");
			for (String string : arr) {
				String num=string.split(",")[0].replace("(", "").trim();
				System.out.println(num);
				list.add(Integer.parseInt(num));
			}
			for (Integer integer : list) {
				System.out.println(integer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
      obj.close();
      
 */

}
