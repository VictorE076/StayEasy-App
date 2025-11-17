package com.stayeasy.stayeasyspringangular.registery;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.Service.UserSessionService;
import com.stayeasy.stayeasyspringangular.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

public class RegController {
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    var user = new User();
    user.setUsername(request.getUsername());
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }
    user.setEmail(request.getEmail());
    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      return ResponseEntity.badRequest().body("Not a valid email adress");
    }
    if (userRepository.existByEmail(request.getEmail())) {
      return ResponseEntity.badRequest().body("There is already a user with this email");
    }
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setfullName(request.getfullName());
    user.setRole("user");
    user.setCreatedAt(LocalDateTime.now());
    userRepository.save(user);
    return ResponseEntity.ok("Account created successfully");

  }
}



