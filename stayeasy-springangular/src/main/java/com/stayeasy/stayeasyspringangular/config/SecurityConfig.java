package com.stayeasy.stayeasyspringangular.config;

import com.stayeasy.stayeasyspringangular.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /// !!! TEMP User in-memory (until Radu's work done with "User" entity from DB)
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.builder()
      .username("user")                                           /// username (TEST)
      .password(passwordEncoder.encode("password"))   /// password (TEST)
      .roles("USER")
      .build();

    return new InMemoryUserDetailsManager(user);
  }

  // "Password Encoder" used for Authentication (BCrypt hashing algorithm)
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // "AuthenticationManager" based on the above config
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    throws Exception {
    return config.getAuthenticationManager();
  }

  // SecurityFilterChain's config (Who is allowed where?)
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
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
      // adding the "jwtAuthenticationFilter" before UsernamePasswordAuthenticationFilter
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}

