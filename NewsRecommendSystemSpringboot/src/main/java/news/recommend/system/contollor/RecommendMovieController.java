package news.recommend.system.contollor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpSession;

import news.recommend.system.Util.linuxContollorUtil;
import news.recommend.system.exception.SSMException;
import news.recommend.system.mapper.TopTenMoviesMapper;
import news.recommend.system.pojo.HotMoviePojo;
import news.recommend.system.pojo.MovieLikeRecommendPojo;
import news.recommend.system.pojo.MoviesPojo;
import news.recommend.system.pojo.RatingsPojo;
import news.recommend.system.pojo.TopTenMoviesPojo;
import news.recommend.system.pojo.User;
import news.recommend.system.service.HotMoviesIService;
import news.recommend.system.service.IUserService;
import news.recommend.system.service.MoviesIService;
import news.recommend.system.service.RatingsIService;
import news.recommend.system.service.TopTenMoviesIService;
import news.recommend.system.service.impl.MovieLikeRecommendService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.ibm.icu.text.SimpleDateFormat;

@Controller
public class RecommendMovieController {
	@Autowired
	private IUserService userService;

	/**
	 * 登录或注册后,选择标签操作,对相关内容进行更新
	 * 
	 * @param genners
	 * @param session
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/register2")
	public void setTheUserGenners(String genners, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		System.out.println(genners + "obj: " + user);
		if (genners != null || genners.equals("")) {
			String[] arr=genners.trim().split(",");
			HashSet<String>set=new HashSet<String>();
			for (String string : arr) {
				set.add(string);
			}
			String str="";
			for (String string : set) {
				str=str+string+",";
			}
			user.setGenner(str);
		}
		try {
			userService.addGenner(user);
		} catch (SSMException e) {
			e.printStackTrace();
		}
//		return "/jsp/success";
	}

	@RequestMapping("/register3")
	public String register3(String genners, HttpSession session) {
		return "/jsp/success";
	}
	
	
	@Autowired
	private TopTenMoviesIService mapper;


	@Autowired
	private MoviesIService movies;

	/**
	 * 根据标签推荐电影
	 * 
	 * 1.获取user表中用户喜欢的类别
	 * 2.获取用户喜欢的类别中的热门电影集合
	 * 3.根据id找到内容-待开发,需要爬虫
	 * 
	 * @param session
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getTheUserGenners")
	public List<MoviesPojo> getTheUserGenners(HttpSession session) {
		User user = (User) session.getAttribute("loginUser");// session中用户登录的信息
		List<MoviesPojo> moviesList=new ArrayList<MoviesPojo>();
		try {
			User newuser = userService.select4username2showGenner(user);// 找到genner
			String[] str = newuser.getGenner().split(",");
			List<TopTenMoviesPojo> list = new ArrayList<TopTenMoviesPojo>();// 存储符合要求的信息
			for (String string : str) {
				TopTenMoviesPojo obj = new TopTenMoviesPojo();
				obj.setGenners(string);
				list.add(mapper.selectByGenner(obj));
			}
			for (TopTenMoviesPojo topTenMoviesPojo : list) {
				System.out.println(topTenMoviesPojo.getTopTenGennersCollection());// 展示信息 genner->id-rating-,
				String[] s=topTenMoviesPojo.getTopTenGennersCollection().split(",");
				for (String string : s) {
					MoviesPojo movie=new MoviesPojo();
					movie.setMovieId(string.split("-")[0]);
					System.out.println(string.split("-")[0]);
					MoviesPojo li=movies.selectMovie(movie);
						System.out.println(li);//最终推荐的电影信息
						moviesList.add(li);
				}
			}
		} catch (SSMException e) {
			e.printStackTrace();
		}
		return moviesList;
	}

	@Autowired
	private MovieLikeRecommendService movielike;
	
	@Autowired
	private MoviesIService movie;

	/**
	 * 根据用户最近点击-评分过的电影进行推荐
	 * 如果使用mysql,效率低因为宽表存所有用户的数据,查询非常慢除非使用hbase
	 * 所以这里使用redis构建实时管道
	 * @param session
	 * @throws Exception
	 */
	/**
	 * 1.根据最新时间的操作获得最新的用户的行为,只拿出最近的一条来
	 * 2.获得最新行为的电影ID
	 * 3.根据相似度矩阵查找相似电影推荐,点击过的电影为-1.0,评分的为0-5.0
	 * 4.根据推荐优先级实时推荐响应点击(过滤掉看过的电影)
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getHistoryRating")
	public List<MoviesPojo> getHistoryRating(HttpSession session) throws Exception {
		Jedis jedis=new Jedis(RatingContollor.redisIP,6379);
		User user = (User) session.getAttribute("loginUser");// session中用户登录的信息
//		jedis.lpush(key)存值
		String s=jedis.lpop(user.getId()+"");//取值只取第一个
		if (s!=null) {//为空说明没操作,就不推荐东西了,不为空的时候才能推荐
			int theId=Integer.parseInt(s);
			MovieLikeRecommendPojo pojo=new MovieLikeRecommendPojo();
			pojo.setMovieId(theId);
			List<Integer> movieList= new ArrayList<Integer>();
			List<MovieLikeRecommendPojo> list=movielike.selectMovieLikeList(pojo);
			for (MovieLikeRecommendPojo movieLikeRecommendPojo : list) {
				System.out.println(movieLikeRecommendPojo);
				//MovieLikeRecommendPojo [movieId=9, recommend_collections=71-1这个是相似度不是评分.0,204-1.0,251-1.0,667-1.0,980-1.0,1102-1.0,1110-1.0,1170-1.0,1424-1.0,1434-1.0,1497-1.0,1599-1.0,2196-1.0,2258-1.0,2534-1.0,2756-1.0,2817-1.0,2965-1.0,3283-1.0,3444-1.0,3541-1.0,3769-1.0,4001-1.0,4199-1.0,4200-1.0,4387-1.0,4441-1.0,4531-1.0,4542-1.0,4568-1.0,4569-1.0,4614-1.0,4630-1.0,4636-1.0,4637-1.0,4651-1.0,4853-1.0,4866-1.0,4947-1.0,4950-1.0,5084-1.0,5155-1.0,5156-1.0,5212-1.0,5376-1.0,5409-1.0,5580-1.0,5922-1.0,5934-1.0,6095-1.0,6130-1.0,6177-1.0,6417-1.0,6723-1.0,7192-1.0,7310-1.0,7376-1.0,7704-1.0,7723-1.0,7892-1.0,7899-1.0,8816-1.0,]
				String[] arr=movieLikeRecommendPojo.getRecommend_collections().split(",");
				for (String string : arr) {
					movieList.add(Integer.parseInt(string.split("-")[0]));
				}
			}
			List<MoviesPojo> recommendList=new ArrayList<MoviesPojo>();
			if (movieList.size()>9) {
				for (Integer integer : movieList.subList(0, 9)) {
					MoviesPojo p=new MoviesPojo();
					p.setMovieId(integer+"");
					MoviesPojo l=movie.selectMovie(p);
						recommendList.add(l);
				}
			}else {
				for (Integer integer : movieList.subList(0, movieList.size())) {
					MoviesPojo p=new MoviesPojo();
					p.setMovieId(integer+"");
					MoviesPojo l=movie.selectMovie(p);
						recommendList.add(l);
				}
				
			}
			
			for (MoviesPojo moviesPojo : recommendList) {
				System.out.println("推荐列表"+moviesPojo);//最终推荐的电影信息
			}
			return recommendList;
		}
		return null;
		
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
//			Date endDate = sdf.parse(ratingsPojo.getTimestamp()+"000");

	}
	
	/**
	 * 历史热度推荐
	 */
	@Autowired
	private HotMoviesIService hotmovies;

	@Autowired
	private MoviesIService mo;

	
		@ResponseBody
		@RequestMapping("/getHistoryHotMovies")
		public List<MoviesPojo> getHistoryHotMovies(){
			List<news.recommend.system.pojo.HotMoviePojo> list=	hotmovies.selectAllHotMovies();
			List<MoviesPojo> moviesList=new ArrayList<MoviesPojo>();
			for (HotMoviePojo hotMoviePojo : list) {
				System.out.println(hotMoviePojo);
				MoviesPojo pojo=new MoviesPojo();
				pojo.setMovieId(hotMoviePojo.getMovieId()+"");
				moviesList.add(mo.selectMovie(pojo));
			}
			return moviesList;
			
		}
		
		/**
		 * linux部署sparkmlib封装好的方法和模型
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("/alsRecommend")
		public List<MoviesPojo> alsRecommend(HttpSession session){
			String shell="cd /root/deeplearning/movies  && java -cp deeplearning-assembly-0.1.0-SNAPSHOT.jar com.wtx.job014.movie.demo /root/deeplearning/model/recommendMovies4User ";
			User user = (User) session.getAttribute("loginUser");// session中用户登录的信息
			String finalShell=shell+" "+user.getId()+" 10";
			linuxContollorUtil linux=new linuxContollorUtil();
			List<Integer> list=linux.getALSRecommendsStrUtil(RatingContollor.linuxIP, finalShell);
			List<MoviesPojo> result=new ArrayList<MoviesPojo>();
			for (Integer integer : list) {
				MoviesPojo pojo=new MoviesPojo();
				String movieId=integer+"";
				pojo.setMovieId(movieId.trim());
				result.add(movie.selectMovie(pojo));
			}
				return result;
			
		}
}
