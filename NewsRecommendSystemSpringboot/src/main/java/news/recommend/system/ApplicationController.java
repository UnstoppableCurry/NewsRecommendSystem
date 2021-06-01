package news.recommend.system;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

//(exclude = {DataSourceAutoConfiguration.class}) 
@SpringBootApplication
@ComponentScan(basePackages = { "news.recommend.system" })
public class ApplicationController {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationController.class, args);
//		try {
//			Runtime.getRuntime()
//					.exec("rundll32 url.dll,FileProtocolHandler http://127.0.0.1:8080/");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
