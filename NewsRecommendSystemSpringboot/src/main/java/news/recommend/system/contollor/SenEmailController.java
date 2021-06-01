package news.recommend.system.contollor;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import news.recommend.system.service.IEmailCheckCodeService;
import news.recommend.system.service.impl.EmailCheckCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/email",produces = "text/html;charset=UTF-8;")
public class SenEmailController {
	private String sucmes="1";
	//"获取成功"
	private String falmes="0";
	//时间小于60秒,请等待
	
	int seconde=1;
	@Autowired(required=false)
    private   JavaMailSender javaMailSender;
	
	
    private static String emailFrom="2537156207@qq.com";

private	Random random = new Random();
private StringBuilder codeBuilder = new StringBuilder();
private String coder;

    
    @RequestMapping("send")
    public String senMsg( HttpServletRequest request,HttpSession session, Model model) throws UnsupportedEncodingException{
    	System.out.println("javaMailSender"+javaMailSender);
    	System.out.println("emailFrom"+emailFrom);
    	String email=request.getParameter("email");
    	if(!"".equals(request.getParameter("email"))){
    		SimpleMailMessage message = new SimpleMailMessage();
            //发件人的邮箱地址
            message.setFrom(emailFrom);
            //收件人的邮箱地址
            message.setTo(email);
        	ServletContext application=request.getServletContext();	
            String code = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
        	int num = 4;
        	for (int i = 0; i < num; i++) {
        		int index = random.nextInt(code.length());
        		char value = code.charAt(index);
        		codeBuilder.append(value);
        	}
        	System.out.println("codeBuilder " +codeBuilder.toString());
    		
    		coder=""+codeBuilder;
    		System.out.println("coder"+coder);
    		codeBuilder.delete(0, codeBuilder.length()+1);
        	//判断两次时间间隔是否大于一分钟
        	//如果是第一次验证
    		System.out.println(session.getAttribute("userIP"));
    		System.out.println(request.getRemoteAddr()+"------");
    		//判断两次时间间隔是否大于一分钟
        	//如果是第一次验证
    		
    		if(session.getAttribute("emailCodeTime")!=null){
    		//session不为空
                long beforeEmailCodeTime = (long)session.getAttribute("emailCodeTime");
                Date b= new Date();
                long localTime = b.getTime();
                String userIP= request.getRemoteAddr();
                IEmailCheckCodeService emailCheckCodeService=new EmailCheckCodeService();
                if (emailCheckCodeService.checkCodeTime(localTime, beforeEmailCodeTime, seconde)) {
                	//判断时间间隔
                	
                	//发送邮件 
                	//将数据放到session和application中
               	 session.setAttribute("emailCodeTime",localTime);
               	 session.setAttribute("userIP", request.getRemoteAddr());
               	 application.setAttribute("userIP", userIP);
               	 application.setAttribute("emailCodeTime", localTime);

                 //邮件主题
                 message.setSubject("邮箱验证码");
                 //邮件内容
                 message.setText("您的4位验证码是："+coder);
                 //发送邮件
                 javaMailSender.send(message);
                    return sucmes;
       		}else{
    	return falmes;
    }
        	
    }else{
    //session为空
    //先进行ip地址校验
    //如果ip已经存在,说明是同一用户重复登录,需要进行作用域生命周期提升比较
    //如果ip不存在,那么说明是新用户,直接获取验证码,但是要在session和application中进行比较
    	
    if(request.getRemoteAddr().equals(application.getAttribute("userIP"))){
        	//id地址一致,说明是一个用户,要进行时间校验,因为他关闭了浏览器
            		long beforeEmailCodeTime = (long)application.getAttribute("emailCodeTime");
                    Date b= new Date();
                    long localTime = b.getTime();
                    IEmailCheckCodeService emailCheckCodeService=new EmailCheckCodeService();

                    	if (emailCheckCodeService.checkCodeTime(localTime, beforeEmailCodeTime, seconde)) {
                   	 

                   	 session.setAttribute("emailCodeTime",localTime);
                   	 session.setAttribute("userIP", request.getRemoteAddr());
                   	application.setAttribute("emailCodeTime", localTime);
                   	//发送邮件
                    //邮件主题
                    message.setSubject("邮箱验证码");
                    //邮件内容
                    message.setText("您的4位验证码是："+coder);
                    //发送邮件
                    javaMailSender.send(message);
                        return sucmes;
                    	}else{
           			//id一致,且时间验证小于60秒,那么就是这个用户在重复提交,所以就不能获取验证码
                    		return falmes;
                    	}
    	}else {
    //ip地址不一致,要放行,因为他是第一次来的
    		 Date b= new Date();
             long localTime = b.getTime();
           

        	 session.setAttribute("emailCodeTime",localTime);
        	 session.setAttribute("userIP", request.getRemoteAddr());
        	application.setAttribute("emailCodeTime", localTime);
        	application.setAttribute("userIP",request.getRemoteAddr());
        	//发送邮件
        	  //邮件主题
            message.setSubject("邮箱验证码");
            //邮件内容
            message.setText("您的4位验证码是："+coder);
            //发送邮件
            javaMailSender.send(message);
             return sucmes;

    				}
        		}
        	
    	}else {
			return falmes;
		}
    }

    @RequestMapping("/ajax")
	public String emailCheckCode(HttpServletRequest request){
		String a=request.getParameter("code");
		System.out.println("coder:"+coder);
		System.out.println("获取到邮箱验证码的值"+a);
		String code=codeBuilder.toString();
		if(coder.equalsIgnoreCase(a.trim())){
			System.out.println("成功");
			return sucmes;
		}else {
			System.out.println("失败");
			return falmes;
		}
		
	}
}