package news.recommend.system.mapper;

import java.util.List;

import news.recommend.system.exception.SSMException;
import news.recommend.system.pojo.User;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {
	public User loadByUsername(String username);

	public User loadByEmail(String email);

	public void updateByEmail(User user);
	
	public List<User> list();
	
	public void add(User user);
	
	public void delete(int id);
	
	public void addGenner(User user) throws SSMException;

	public User select4username2showGenner(User user) throws SSMException;

}
