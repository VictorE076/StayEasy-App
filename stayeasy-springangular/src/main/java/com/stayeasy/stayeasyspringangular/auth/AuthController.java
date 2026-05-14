package com.stayeasy.stayeasyspringangular.auth;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.Service.UserSessionService;
import com.stayeasy.stayeasyspringangular.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final UserSessionService sessionService;

  public AuthController(
    AuthenticationManager authenticationManager,
    JwtService jwtService,
    UserRepository userRepository,
    UserSessionService sessionService
  ) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.sessionService = sessionService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
      )
    );

    var user = userRepository.findByUsername(request.getUsername())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//    UserSession session = sessionService.createSession(user);
//    String jwtToken = jwtService.generateToken(user, session.getId());

    UserSession session = sessionService.createSession(user, request.isRememberMe());
    String jwtToken = jwtService.generateToken(user, session.getId(), request.isRememberMe());

    return ResponseEntity.ok(new AuthResponse(jwtToken));
  }

  // Close the session when the User logs out successfully!
  /// localhost:8080/api/auth/logout?sessionId=<SID>
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(@RequestParam String sessionId) {
    sessionService.logout(sessionId);

    // { "message" : "Session closed" } (JSON format) sent to frontend Angular
    return ResponseEntity.ok(Map.of("message", "Session closed"));
  }
}
