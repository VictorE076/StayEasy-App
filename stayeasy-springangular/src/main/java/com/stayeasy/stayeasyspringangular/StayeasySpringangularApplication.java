package com.stayeasy.stayeasyspringangular;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.config.JwtProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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


//  test login
  @Bean
  public CommandLineRunner updatePasswordAtStartup(UserRepository userRepository, PasswordEncoder encoder) {
    return args -> {
      String username = "testuser";       // <-- userul din DB
      String rawPassword = "password123"; // <-- parola pe care vrei s-o setezi

      System.out.println("🔐 Updating password for user: " + username);

      Optional<User> optionalUser = userRepository.findByUsername(username);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        String hashed = encoder.encode(rawPassword);

        // adaptează dacă atributul din entitatea User are alt nume
        user.setPasswordHash(hashed);
        userRepository.save(user);

        System.out.println("✅ Password updated successfully for user '" + username + "'");
        System.out.println("💾 Stored bcrypt hash: " + hashed);
      } else {
        System.out.println("⚠️ User '" + username + "' not found in database. No changes made.");
      }
    };
  }

}
