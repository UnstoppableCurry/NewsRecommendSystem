package news.recommend.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface MovieLikeRecommendMapper {
	public List<news.recommend.system.pojo.MovieLikeRecommendPojo> selectMovieLikeList(news.recommend.system.pojo.MovieLikeRecommendPojo pojo);

}



	
