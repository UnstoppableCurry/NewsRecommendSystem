package news.recommend.system.service;

import java.util.List;

import news.recommend.system.pojo.TopTenMoviesPojo;


public interface TopTenMoviesIService {
	public List<news.recommend.system.pojo.TopTenMoviesPojo> selectAllMovies();
	public news.recommend.system.pojo.TopTenMoviesPojo selectByGenner(TopTenMoviesPojo obj);
}
