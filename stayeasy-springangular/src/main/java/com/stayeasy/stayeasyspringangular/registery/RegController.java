package com.stayeasy.stayeasyspringangular.registery;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegController {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(RegController.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {

    // Verificare username
    if (userRepository.existsByUsername(request.getUsername())) {

      logger.warn("Registration failed: username already exists: {}", request.getUsername());

      throw new BadRequestException("Username already exists");
    }

    // Verificare email duplicat
    if (userRepository.existsByEmail(request.getEmail())) {

      logger.warn("Registration failed: email already used: {}", request.getEmail());

      throw new BadRequestException("Email already used");
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

    logger.info("New account created successfully for username {}", user.getUsername());

    return ResponseEntity.ok(Map.of("message", "Account created successfully"));
  }
}
