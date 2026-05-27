package com.BagnSave.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@RestController
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// TODO: move this endpoint to a dedicated controller class e.g HelloController.java
	@GetMapping("/hello")
	public String hello() {
		return "Hello from the backend!";
	}

	// TODO: move this to config/Corsconfig.java
	// It needs to stay as a @Bean but should live in its own @Configuration class
	// Also update the allowed origin to your vercel URL when deploying
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// TODO: move allowed origins to application.properties so it can differ per environment
		config.addAllowedOrigin("http://localhost:5171");
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
