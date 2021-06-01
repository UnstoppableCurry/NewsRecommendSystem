package news.recommend.system.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import news.recommend.system.exception.SSMException;
import news.recommend.system.pojo.User;



public interface IUserService {

	public List<User> list();

	public User login(User user) throws SSMException;
	
	public void register(User user) throws SSMException;
	
	public boolean forgetpassword(String email, HttpSession session);

	public void updateByEmail(User user) throws SSMException;

	
	public void addGenner(User user) throws SSMException;

	public User select4username2showGenner(User user) throws SSMException;

}
