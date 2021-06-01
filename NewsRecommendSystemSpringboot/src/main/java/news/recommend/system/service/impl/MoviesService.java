package news.recommend.system.service.impl;

import java.util.List;

import news.recommend.system.mapper.MoviesMapper;
import news.recommend.system.pojo.MoviesPojo;
import news.recommend.system.service.MoviesIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MoviesService implements MoviesIService {

	@Autowired
	private MoviesMapper mapper;
	@Override
	public MoviesPojo selectMovie(MoviesPojo pojo) {
		return mapper.selectMovie(pojo);
	}
	@Override
	public List<MoviesPojo> selectAllMovie() {
		return mapper.selectAllMovie();
	}
}

