package news.recommend.system.contollor;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import javax.servlet.http.HttpSession;

import news.recommend.system.Util.NewsRecommendSpider;
import news.recommend.system.pojo.MoviesPojo;
import news.recommend.system.pojo.User;
import news.recommend.system.service.MoviesIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

@Controller
public class RatingContollor   {
	public static final String redisIP = "192.168.1.8";
	public static final String linuxIP = "192.168.1.8";
	@Autowired
	private MoviesIService movie;

	/**
	 * 通过id查询电影信息
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMovieByID")
	public MoviesPojo getMovieByID(String id) {
		MoviesPojo pojo = new MoviesPojo();
		pojo.setMovieId(id);
		return movie.selectMovie(pojo);
	}

	/**
	 * 通过点击电影信息,查看电影(新闻)详细内容
	 * 
	 * @return
	 */
	@RequestMapping("/getAllNews")
	public String getAllNews(HttpSession session, String id, Model model) {
		String time=System.currentTimeMillis()/1000+"";
		Jedis jedis = new Jedis(redisIP, 6379);
		User user = (User) session.getAttribute("loginUser");// session中用户登录的信息
		jedis.lpush(user.getId() + time, id);
		jedis.lpush(user.getId()+"", id);//实时推荐
		MoviesPojo po = new MoviesPojo();
		po.setMovieId(id);
				try {
					NewsRecommendSpider.getOnceSpiderStrData(movie.selectMovie(po)
							.getTitle(), Integer.parseInt(movie.selectMovie(po)
							.getMovieId()), user.getId() + time, user.getId()+"data");
				} catch (Exception e) {
					e.printStackTrace();
				}
 
			 model.addAttribute("movie", movie.selectMovie(po).getTitle());
				System.out.println(movie.selectMovie(po).getTitle());
				String firstNews=jedis.lpop(user.getId() + time).replace("null", "");
				int num=jedis.llen(user.getId() + "data").intValue();
				if (num==1||num==0) {
					System.out.println("第一次不替换" +firstNews);
					System.out.println(jedis.lpop(user.getId() + time));
					model.addAttribute("news",firstNews.replace("null", ""));
				}else {
					System.out.println("第二次开始替换");
					String lastNews=jedis.rpop(user.getId() + "data").replace("null", "");
//					System.out.println("这次" +firstNews);
//					System.out.println("下次"+lastNews);
					model.addAttribute("news",firstNews.replace(lastNews,""));
					System.out.println(jedis.lpop(user.getId() + time));
				}
				
		return "/jsp/2index";
	}

	/**
	 * 爬虫线程问题,并行度不能解决,被封ip没办法,此方法已经废弃
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectallmoviesdemo")
	public int selectallmoviesdemo() {
		List<MoviesPojo> list = movie.selectAllMovie().subList(1, 5);
		int i = 0;
		for (MoviesPojo moviesPojo : list) {
			if (Integer.parseInt(moviesPojo.getMovieId()) != 9) {
				System.out.println(moviesPojo.getTitle());
				i++;
				try {
					NewsRecommendSpider.getSpiderStrData(moviesPojo.getTitle(),
							Integer.parseInt(moviesPojo.getMovieId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}
	/**
	 * 多线程
	 *
	 * 
	 * 
	 * Jedis jedis = new Jedis(redisIP, 6379); User user = (User)
	 * session.getAttribute("loginUser");// session中用户登录的信息
	 * jedis.lpush(user.getId() + "", id); MoviesPojo po = new MoviesPojo();
	 * po.setMovieId(id);
	 * 
	 * final CyclicBarrier barrier = new CyclicBarrier(1, new Runnable() {
	 * 
	 * @Override public void run() { model.addAttribute("movie",
	 *           movie.selectMovie(po).getTitle());
	 *           System.out.println(movie.selectMovie(po).getTitle());
	 *           model.addAttribute("news", jedis.lpop(user.getId() + "data")
	 *           .replace("null", ""));
	 *           System.out.println(jedis.lpop(user.getId() + "data")); } });
	 *           Thread a2 = new Thread(new Runnable() {
	 * @Override public void run() { try {
	 *           NewsRecommendSpider.getOnceSpiderStrData(
	 *           movie.selectMovie(po).getTitle(), Integer
	 *           .parseInt(movie.selectMovie(po) .getMovieId()), user.getId() +
	 *           "data"); barrier.await(); } catch (Exception e) {
	 *           e.printStackTrace(); }
	 * 
	 *           } });
	 * 
	 *           return "/jsp/2index";
	 * 
	 * 
	 * 
	 */
}
