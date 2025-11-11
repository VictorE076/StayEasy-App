package com.stayeasy.stayeasyspringangular.auth;

import com.stayeasy.stayeasyspringangular.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
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

    // If no exception thrown, then the authentication was successful
    UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
    String jwtToken = jwtService.generateToken(user); // generating a "jwtToken" for the loaded user

    AuthResponse response = new AuthResponse(jwtToken);
    return ResponseEntity.ok(response);
  }

}

