package com.stayeasy.stayeasyspringangular.config;

import com.stayeasy.stayeasyspringangular.Service.DatabaseUserDetailsService;
import com.stayeasy.stayeasyspringangular.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.CommandLineRunner;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /// !!! TEMP User in-memory (until Radu's work done with "User" entity from DB)
//  @Bean
//  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//    UserDetails user = User.builder()
//      .username("user")                                           /// username (TEST)
//      .password(passwordEncoder.encode("password"))   /// password (TEST)
//      .roles("USER")
//      .build();
//
//    return new InMemoryUserDetailsManager(user);
//  }

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final DatabaseUserDetailsService customUserDetailsService;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        DatabaseUserDetailsService customUserDetailsService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.customUserDetailsService = customUserDetailsService;
  }

  // "Password Encoder" used for Authentication (BCrypt hashing algorithm)
  // Different (new) password hash for each login
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new Sha256PasswordEncoder();
//  }


  // "AuthenticationManager" based on the above config
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    throws Exception {
    return config.getAuthenticationManager();
  }

  // SecurityFilterChain's config (Who is allowed where?)
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // deactivating CSRF for REST API
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // public endpoints (without any token)
        .requestMatchers(
          "/api/auth/**",
          "/api/health-check",
          "/",
          "/index.html",
          "/browser/**",
          "/favicon.ico"
        ).permitAll()
        // everything else requires authentication (a valid JWT token in the header)
        .anyRequest().authenticated())
      .userDetailsService(customUserDetailsService)
      // adding our "jwtAuthenticationFilter" before UsernamePasswordAuthenticationFilter
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /// PRINT TEST
  @Bean
  public CommandLineRunner printBCryptPasswordHash(PasswordEncoder encoder) {
    return args -> {
      String raw = "password123";
      String encoded = encoder.encode(raw);
      System.out.println(">>> BCrypt HASH FOR '" + raw + "' = " + encoded);
    };
  }

}

