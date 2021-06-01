package news.recommend.system.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

public class NewsRecommendSpider implements PageProcessor {
	public static void main(String[] args) throws Exception {
		getSpiderStrData("伊朗", 1);
	}
	
	volatile  static String fileName;
	volatile  static int id;  //监控爬虫循环次数
	volatile  static String newsStr;
	volatile  static String userId;
	volatile  static String lastid;

	
	private Site site = Site.me().setRetryTimes(1).setSleepTime(1);

	public Site getSite() {
		return site;
	}

	public void process(Page page) {

		List<String> list = page.getHtml().css("h3.t").all();
		System.out.println("开始输出");
		for (String string : list) {
//			System.out.println(string);
			newsStr=newsStr+string;
		}
		System.out.println("结束");
		  page.putField("thedata", newsStr);
		  page.putField("fileName", fileName);
		  page.putField("movieID", id+"");
		  page.putField("userId", userId);
		  page.putField("lastid", lastid);
	}

	
	/**
	 * 传入标题,电影ID,爬虫爬取数据保存至数据库
	 * @param movieName
	 * @param id
	 * @throws Exception
	 */
	public static void getSpiderStrData(String movieName,int id) throws Exception{

		// 创建爬虫实体类
		NewsRecommendSpider selemium = new NewsRecommendSpider();
		selemium.setFileName(movieName);
		selemium.setId(id);
		// 设置selemium浏览器配置驱动
		SeleniumDownloader seleniumDownloader = new SeleniumDownloader(
				"G:\\爬虫\\drive\\chromedriver.exe");
		seleniumDownloader.setSleepTime(5000);
		// 配置当前浏览器配置
		System.setProperty("selenuim_config",
				"G:\\workspace\\Git\\webmagic\\config.ini");
		// request类型配置,使用responsebody配置请求头,调用method方法选择post/get请求
		Request request = new Request();
		// 设置被爬取页面
		String st = "https://www.baidu.com/s?ie=utf-8&word=";
		String str=st+movieName+"最新资讯";
		// spider对象用于监控
		Spider obj = Spider.create(selemium).addUrl(str)
				.setDownloader(seleniumDownloader).addPipeline(new mysqlPipeline()).thread(1);
		obj.run();
		obj.stop();
	}
	
	
	public static void getOnceSpiderStrData(String movieName,int id,String lastid,String userId) throws Exception{

		// 创建爬虫实体类
		NewsRecommendSpider selemium = new NewsRecommendSpider();
		selemium.setFileName(movieName);
		selemium.setId(id);
		selemium.setUserId(userId);
		selemium.setLastid(lastid);//上一次重复的解决爬虫的数据线程安全问题
		// 设置selemium浏览器配置驱动
		SeleniumDownloader seleniumDownloader = new SeleniumDownloader(
				"G:\\爬虫\\drive\\chromedriver.exe");
//		seleniumDownloader.setSleepTime(20);
		// 配置当前浏览器配置
		System.setProperty("selenuim_config",
				"G:\\workspace\\Git\\webmagic\\config.ini");
		// request类型配置,使用responsebody配置请求头,调用method方法选择post/get请求
		Request request = new Request();
		// 设置被爬取页面
		String st = "https://www.baidu.com/s?ie=utf-8&word=";
		String str=st+movieName+"最新资讯";
		// spider对象用于监控
		Spider obj = Spider.create(selemium).addUrl(str)
				.setDownloader(seleniumDownloader).addPipeline(new mysqlPipeline()).thread(3);
		obj.run();
		obj.clearPipeline();
		//start()/runAsync()	异步启动，当前线程继续执行坑爹玩意曹尼玛...
		obj.stop();
	}
		public void setSite(Site site) {
			this.site = site;
		}
		public NewsRecommendSpider() {
			super();
		}

		public static String getFileName() {
			return fileName;
		}

		public static void setFileName(String fileName) {
			NewsRecommendSpider.fileName = fileName;
		}

		public static int getId() {
			return id;
		}

		public static void setId(int id) {
			NewsRecommendSpider.id = id;
		}

		public static String getNewsStr() {
			return newsStr;
		}

		public static void setNewsStr(String newsStr) {
			NewsRecommendSpider.newsStr = newsStr;
		}

		public static String getUserId() {
			return userId;
		}

		public static void setUserId(String userId) {
			NewsRecommendSpider.userId = userId;
		}

		public static String getLastid() {
			return lastid;
		}

		public static void setLastid(String lastid) {
			NewsRecommendSpider.lastid = lastid;
		}
		

}
