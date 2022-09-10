package com.hub.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.hub.ecommerce.repository")
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:5000","http://127.0.0.1","http://0.0.0.0","https://website.com")
						.allowCredentials(true)
						.exposedHeaders("set-cookie")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//				registry.addMapping("/admin/**").allowedOrigins("http://localhost:5000","http://127.0.0.1","http://0.0.0.0/**","https://trepechyjewels.com")
//						.allowCredentials(true)
//						.exposedHeaders("set-cookie")
//						.allowedMethods("GET","POST","UPDATE","DELETE");
			}
		};
	}
}
