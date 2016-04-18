package net.czela.bank;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by jirsakf on 18.4.2016.
 */
@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class Main extends SpringBootServletInitializer {
	public static void main(String[] args) {
		new SpringApplicationBuilder(Main.class)
//				.profiles("devel")
				.run(args);
	}
}
