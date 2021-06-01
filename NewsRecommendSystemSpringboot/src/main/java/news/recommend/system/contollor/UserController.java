package news.recommend.system.contollor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import news.recommend.system.exception.SSMException;
import news.recommend.system.pojo.User;
import news.recommend.system.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@RestController

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private IUserService userService;

	@RequestMapping("/list")
	public String list(HttpSession session, Model model) {
		List<User> users = userService.list();
		model.addAttribute("users", users);
		return "/jsp/user/user/list";
	}

	@RequestMapping("userlogin")
	public String login(String username, String password, HttpSession session)
			throws SSMException {
		System.out.println("获取登录数据");
		User user = new User(username, password);
		User loginUser = userService.login(user);
		System.out.println(user);
		session.setAttribute("loginUser", loginUser);
		System.out.println(session.getAttribute("loginUser"));
		return "redirect:/index";
	}

	@ResponseBody
	@RequestMapping("forgetpassword")
	public String forgetpassword(String email, HttpSession session)
			throws SSMException {

		// String email=(String) request.getAttribute("email");
		System.out.println("forgetpassword获取邮箱成功为:  " + email);
		if (userService.forgetpassword(email, session)) {
			// 邮箱如果存在,那么就可以进行密码更改

			return "1";
		} else {
			// 邮箱如果不存在那么就不能更改密码
			return "0";
		}
	}

	@RequestMapping("/gitpassword")
	public String gitpassword() {
		System.out.println("路由gitpassword请求成功");
		return "/jsp/user/gitpassword";
	}

	@RequestMapping("/password")
	public String password(HttpSession session,
			@RequestParam("password") String password) throws SSMException {
		// String password=(String) request.getAttribute("password");
		System.out.println("路由password请求成功");
		User user = (User) session.getAttribute("user");
		// user.setPassword(password);
		System.out.println("password" + password);
		session.setAttribute("user", user);
		System.out.println(session.getAttribute("user"));
		userService.updateByEmail(user);
		// return "redirect:/login";
		// return "/success";

		return "/jsp/success";
	}
}
