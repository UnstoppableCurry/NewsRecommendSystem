package news.recommend.system.service.impl;


import java.util.List;

import news.recommend.system.mapper.HotMoviesMapper;
import news.recommend.system.pojo.HotMoviePojo;
import news.recommend.system.service.HotMoviesIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotMoviesService implements HotMoviesIService{
	@Autowired
	private HotMoviesMapper mapper;
	
	@Override
	public List<HotMoviePojo> selectAllHotMovies() {
		return mapper.selectAllHotMovies();
	}

}
