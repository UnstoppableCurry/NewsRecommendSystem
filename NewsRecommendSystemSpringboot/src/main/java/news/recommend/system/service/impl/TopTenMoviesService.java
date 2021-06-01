package news.recommend.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import news.recommend.system.mapper.TopTenMoviesMapper;
import news.recommend.system.pojo.TopTenMoviesPojo;
import news.recommend.system.service.TopTenMoviesIService;

@Service
public class TopTenMoviesService implements TopTenMoviesIService{
	@Autowired
	private TopTenMoviesMapper mapper;
	
	
	@Override
	public List<TopTenMoviesPojo> selectAllMovies() {
		return mapper.selectAllMovies();
	}


	@Override
	public TopTenMoviesPojo selectByGenner(TopTenMoviesPojo obj) {
		return mapper.selectByGenner(obj);
	}


}
