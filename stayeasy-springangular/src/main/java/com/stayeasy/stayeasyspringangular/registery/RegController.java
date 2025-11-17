package com.stayeasy.stayeasyspringangular.registery;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

    // Verificare username
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    // Verificare email valid
    if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      return ResponseEntity.badRequest().body("Invalid email address");
    }

    // Verificare email duplicat
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.badRequest().body("Email already used");
    }

    // Creare user
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setFullName(request.getFullName());
    user.setRole(Role.GUEST);
    user.setCreatedAt(LocalDateTime.now());

    userRepository.save(user);

    return ResponseEntity.ok("Account created successfully");
  }
}
