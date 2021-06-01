package news.recommend.system.contollor;

import java.util.List;

import javax.servlet.http.HttpSession;

import news.recommend.system.pojo.User;
import news.recommend.system.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/home")
public class HomeController {
	@Autowired
	private IUserService userService;
	@RequestMapping("/homepage2")
	public String homepage(HttpSession session, Model model) {
		List<User> users = userService.list();
		model.addAttribute("users", users);
		return "/jsp/home/homepage2";
	}
	@RequestMapping("/homepage3")
	public String homepage2(HttpSession session, Model model) {
		List<User> users = userService.list();
		model.addAttribute("users", users);
		return "/jsp/home/homepage3";
	}
}
