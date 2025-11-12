package com.stayeasy.stayeasyspringangular.auth;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.Service.UserSessionService;
import com.stayeasy.stayeasyspringangular.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
//  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final UserSessionService sessionService;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService, UserRepository userRepository, UserSessionService sessionService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
//    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.sessionService = sessionService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

    // Validating username AND password, using AuthenticationManager
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
      )
    );

    // Încarcă userul din DB
    User user = userRepository.findByUsername(request.getUsername())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Creează o nouă sesiune activă
    UserSession session = sessionService.createSession(user);

    String jwtToken = jwtService.generateToken(user, session.getId()); // generating a "jwtToken" for the loaded user

    AuthResponse response = new AuthResponse(jwtToken);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestParam String sessionId) {
    sessionService.logout(sessionId);
    return ResponseEntity.ok("Session closed");
  }

}

