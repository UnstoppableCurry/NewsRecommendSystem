package news.recommend.system.mapper;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HotMoviesMapper {
	public List<news.recommend.system.pojo.HotMoviePojo> selectAllHotMovies();
}
