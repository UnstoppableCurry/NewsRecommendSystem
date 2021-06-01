package news.recommend.system.mapper;

import java.util.List;

import news.recommend.system.pojo.TopTenMoviesPojo;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopTenMoviesMapper {
	public List<news.recommend.system.pojo.TopTenMoviesPojo> selectAllMovies();
	public news.recommend.system.pojo.TopTenMoviesPojo selectByGenner(TopTenMoviesPojo obj);
	
}
