package news.recommend.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface RatingsMapper {
	public List<news.recommend.system.pojo.RatingsPojo> selectOnecRating(news.recommend.system.pojo.RatingsPojo pojo);
}