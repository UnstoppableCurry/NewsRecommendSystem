package news.recommend.system.contollor;

import javax.servlet.http.HttpSession;

import news.recommend.system.exception.SSMException;
import news.recommend.system.pojo.User;
import news.recommend.system.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
	@Autowired
	private IUserService userService;

	@RequestMapping({ "/", "/login" })
	public String login() {
		System.out.println("login请求");
		return "/jsp/user/login";
	}

	@RequestMapping("/index")
	public String index1() {
		System.out.println("index请求");

		return "/jsp/index";
	}
	@RequestMapping("/reg")
	public String reg() {
		System.out.println("reg注册请求");

		return "/jsp/user/reg";
	}
	@RequestMapping("forget")
	public String forgetpassword() {
		System.out.println("forget忘记密码请求");

		return "/jsp/user/forget";
	}
	@RequestMapping("/register")
	public String register(String username,String email,String password,String nickname,HttpSession session, Model model) throws SSMException {
		System.out.println("获取注册传入数据成功");
		System.out.println("username  : "+username+"password : "+password+"nickname : "+nickname+"email : "+email);
//		User user=new User(username, password, nickname, email);
		User user = new User(username, password);
		userService.register(user);
		session.setAttribute("loginUser", user);
		//此时如果直接转跳,那么会将后台管理模块直接暴露到外界,所以应该用代理模式将核心封装起来拒绝直接转跳
// 		return "/index";
		return "/jsp/user/reg2";
	}
	
}
