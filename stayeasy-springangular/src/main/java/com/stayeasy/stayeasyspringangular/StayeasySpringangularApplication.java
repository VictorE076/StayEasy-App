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


/// TEST login
  @Bean
  public CommandLineRunner updatePasswordAtStartup(UserRepository userRepository, PasswordEncoder encoder) {
    return args -> {
      String username_test = "testuser";       // <-- userul din DB
      String rawPassword_test = "password123"; // <-- parola pe care vreau s-o setez

      System.out.println("Updating password for user: " + username_test);

      Optional<User> optionalUser = userRepository.findByUsername(username_test);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        String hashed = encoder.encode(rawPassword_test);

        // updateaza daca atributul din entitatea User are alt nume
        user.setPasswordHash(hashed);
        userRepository.save(user);

        System.out.println("MAIN TEST: Password updated successfully for user '" + username_test + "'");
        System.out.println("MAIN TEST: Stored bcrypt hash: " + hashed);
      } else {
        System.out.println("MAIN TEST: User '" + username_test + "' not found in database. No changes made.");
      }
    };
  }

}
