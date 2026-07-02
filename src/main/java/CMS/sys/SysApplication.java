package CMS.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SysApplication {

	public static void main(String[] args) {
		SpringApplication.run(SysApplication.class, args);
	}

}
