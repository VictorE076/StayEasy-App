package com.stayeasy.stayeasyspringangular;

import com.stayeasy.stayeasyspringangular.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@RestController
public class StayeasySpringangularApplication {

	public static void main(String[] args) {
		SpringApplication.run(StayeasySpringangularApplication.class, args);
	}

  @GetMapping("/api/health-check")
  public String healthCheck() {
    return "Health check is OK (5-7 seconds RELOAD time) !";
  }
}
