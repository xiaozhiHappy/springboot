package org.spring.springboot;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.util.DefaultProfileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
/**
 * 
 * @author lzj
 *
 */
@SpringBootApplication
@EnableCaching
@MapperScan("org.spring.springboot.dao")
public class Application extends SpringBootServletInitializer{
	 private static Logger logger = LoggerFactory.getLogger(Application.class);  
    public static void main(String[] args) {
    	SpringApplication app = new SpringApplication(Application.class);
		DefaultProfileUtil.addDefaultProfile(app);
		ApplicationContext appc =  app.run(args);
		Environment env = appc.getEnvironment();
		String ip = "localhost";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://{}:{}\n\t" +
                "----------------------------------------------------------",
          env.getProperty("spring.profiles.active"),ip,
          env.getProperty("server.port"));
		System.out.println("分支合并");
		logger.info("程序启动完成。。。。。。");
    }
}
