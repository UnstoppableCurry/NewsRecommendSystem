package news.recommend.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import news.recommend.system.mapper.RatingsMapper;
import news.recommend.system.pojo.RatingsPojo;
import news.recommend.system.service.RatingsIService;

@Service
public class RatingsService implements RatingsIService {
	@Autowired
	private RatingsMapper mapper;
	

	@Override
	public List<RatingsPojo> selectOnecRating(RatingsPojo pojo) {
		return mapper.selectOnecRating(pojo);
	}
}