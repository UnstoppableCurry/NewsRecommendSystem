package news.recommend.system.service.checkCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Stack;

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
public class CheckCodeServlet extends HttpServlet {

	@RequestMapping("/codeBuilder")
	public void getCheckCode(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("image/jpg");

	int width = 80;
	int height = 40;

	BufferedImage img = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);

	Graphics2D graphic = img.createGraphics();
	graphic.setColor(Color.white);
	graphic.fillRect(0, 0, width, height);

	graphic.setColor(Color.BLACK);
//	graphic.drawRect(0, 0, width, height);

	Font font = new Font("宋体", Font.BOLD + Font.ITALIC,
			(int) (height * 0.8));
	graphic.setFont(font);
	String code = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";

	int num = 4;
	Random random = new Random();
	StringBuilder codeBuilder = new StringBuilder();
	for (int i = 0; i < num; i++) {
		int index = random.nextInt(code.length());
		char value = code.charAt(index);
		codeBuilder.append(value);
	}
	// System.out.println(codeBuilder.toString());

	String checkcode = codeBuilder.toString();


		HttpSession session=request.getSession();
		session.setAttribute("count",checkcode );
		
		
		
		
	for (int i = 0; i < num; i++) {
		 graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		 String c = checkcode.charAt(i)+"";
		 graphic.drawString(c, i*(width/num), height-(random.nextInt(height/3)));
	}

	for (int i = 0; i < (width+height)*2; i++) {
		 graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		 
		 graphic.drawOval(random.nextInt(width), random.nextInt(height), 1, 1);
	}
	
	for (int i = 0; i < 4; i++) {
		 graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		 graphic.drawLine(0, random.nextInt(height), width, random.nextInt(height));
	}
	
	OutputStream os = response.getOutputStream();
	ImageIO.write(img, "jpg", os);
//	request.getRequestDispatcher("CherkServlet").forward(request,response);

}
}
