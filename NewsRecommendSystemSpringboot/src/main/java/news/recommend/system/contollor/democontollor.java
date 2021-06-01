package news.recommend.system.contollor;

import java.util.List;

import javax.servlet.http.HttpSession;

import news.recommend.system.exception.SSMException;
import news.recommend.system.pojo.MovieLikeRecommendPojo;
import news.recommend.system.pojo.MoviesPojo;
import news.recommend.system.pojo.RatingsPojo;
import news.recommend.system.pojo.TopTenMoviesPojo;
import news.recommend.system.pojo.User;
import news.recommend.system.service.IUserService;
import news.recommend.system.service.MoviesIService;
import news.recommend.system.service.RatingsIService;
import news.recommend.system.service.TopTenMoviesIService;
import news.recommend.system.service.impl.MovieLikeRecommendService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class democontollor {
	@Autowired
	private RatingsIService userService;
	/**
	 * 登录或注册后,选择标签操作,对相关内容进行更新
	 * @param genners
	 * @param session
	 */
	@ResponseBody
	@RequestMapping("/selectalldemo")
	public void selectalldemo(String genners, HttpSession session){
		RatingsPojo obj=new RatingsPojo();
		obj.setUserId("20");
				List<RatingsPojo> list=userService.selectOnecRating(obj);
				for (RatingsPojo ratingsPojo : list) {
					System.out.println(ratingsPojo);
				}
	}
}
