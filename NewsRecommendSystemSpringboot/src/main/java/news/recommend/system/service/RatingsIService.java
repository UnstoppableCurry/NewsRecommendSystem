package news.recommend.system.service;

import java.util.List;

public interface RatingsIService {
	public List<news.recommend.system.pojo.RatingsPojo> selectOnecRating(news.recommend.system.pojo.RatingsPojo pojo);
}