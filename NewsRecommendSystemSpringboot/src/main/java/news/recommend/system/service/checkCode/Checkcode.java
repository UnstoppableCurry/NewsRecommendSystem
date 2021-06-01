package news.recommend.system.service.checkCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


import java.util.*;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class Checkcode extends HttpServlet {
	@RequestMapping("checkajax")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

			request.setCharacterEncoding("utf-8");
	        response.setContentType("text/html;charset=utf-8");
		 String code=request.getParameter("code");
	        String truefalse=request.getParameter("truefalse");
	      System.out.println(truefalse);
	      System.out.println(code);
	      HttpSession session=request.getSession();
		String a=	(String) session.getAttribute("count");
		System.out.println(a);
		if(a.equalsIgnoreCase(code)){
			System.out.println("验证码正确");
////			session.setAttribute("truefalse", "1");
			truefalse="1";
//			PrintWriter out = response.getWriter();
//			out.write(truefalse);
//			PrintWriter out = response.getWriter();
//			out.print(false);
//			Map <String,String> data=new HashMap<>();
//			
//			data.put("truefalse", "1");
			response.setContentType("text/html;charset=utf-8");
//			
			response.getWriter().println(truefalse);
			 
			
		}else{
			response.setContentType("text/html;charset=utf-8");
			truefalse="0";
			response.getWriter().println(truefalse);
			System.out.println("验证码错误");
		}
	}
	}