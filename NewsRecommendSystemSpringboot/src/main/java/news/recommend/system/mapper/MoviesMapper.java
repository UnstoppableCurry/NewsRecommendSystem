package news.recommend.system.mapper;

import java.util.List;

import news.recommend.system.pojo.MoviesPojo;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface MoviesMapper {
	public MoviesPojo selectMovie(news.recommend.system.pojo.MoviesPojo pojo);
	public List<MoviesPojo> selectAllMovie();
}

