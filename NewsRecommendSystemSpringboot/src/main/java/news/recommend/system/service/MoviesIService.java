package news.recommend.system.service;

import java.util.List;

import news.recommend.system.pojo.MoviesPojo;

public interface MoviesIService {
	public MoviesPojo selectMovie(news.recommend.system.pojo.MoviesPojo pojo);
	public List<MoviesPojo> selectAllMovie();
}

