package news.recommend.system.service.impl;

import java.util.List;

import news.recommend.system.mapper.HotMoviesMapper;
import news.recommend.system.mapper.MovieLikeRecommendMapper;
import news.recommend.system.pojo.MovieLikeRecommendPojo;
import news.recommend.system.service.MovieLikeRecommendIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieLikeRecommendService  implements MovieLikeRecommendIService{

	@Autowired
	private MovieLikeRecommendMapper mapper;
	
	@Override
	public List<MovieLikeRecommendPojo> selectMovieLikeList(MovieLikeRecommendPojo pojo) {
		return mapper.selectMovieLikeList(pojo);
	}
	
}



	

