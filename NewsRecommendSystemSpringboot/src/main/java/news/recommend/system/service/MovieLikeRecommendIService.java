package news.recommend.system.service;

import java.util.List;

import news.recommend.system.pojo.MovieLikeRecommendPojo;

public interface MovieLikeRecommendIService {
	public List<news.recommend.system.pojo.MovieLikeRecommendPojo> selectMovieLikeList(MovieLikeRecommendPojo pojo);
}



	
