package com.stayeasy.stayeasyspringangular.auth;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.Service.UserSessionService;
import com.stayeasy.stayeasyspringangular.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
//  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final UserSessionService sessionService;
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, UserSessionService sessionService, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
//    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.sessionService = sessionService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {

    System.out.println(">>> LOGIN CALLED for username=" + request.getUsername() + ", password=" + request.getPassword());

    var user = userRepository.findByUsername(request.getUsername())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    System.out.println(">>> DB hash = " + user.getPasswordHash());
    System.out.println(">>> matches(request.password)? = " +
      passwordEncoder.matches(request.getPassword(), user.getPasswordHash()));

    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getUsername(),
          request.getPassword()
        )
      );

      System.out.println(">>> LOGIN CALLED (after authentication) for username=" + request.getUsername());

      UserSession session = sessionService.createSession(user);
      String jwtToken = jwtService.generateToken(user, session.getId());

      return ResponseEntity.ok(new AuthResponse(jwtToken));

    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(">>> AUTH FAILED: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
      return ResponseEntity.status(403).body("Login failed: " + ex.getClass().getSimpleName());
    }
  }


  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestParam String sessionId) {
    sessionService.logout(sessionId);
    return ResponseEntity.ok("Session closed");
  }

}

