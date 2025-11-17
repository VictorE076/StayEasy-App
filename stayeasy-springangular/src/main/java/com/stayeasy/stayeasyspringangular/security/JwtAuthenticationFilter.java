package com.stayeasy.stayeasyspringangular.security;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final UserSessionService sessionService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, UserSessionService sessionService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.sessionService = sessionService;
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
    throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // No header OR no header starting with "Bearer " (treated as NOT logged in) -> go further
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Extracting token (without "Bearer ")
    jwt = authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    // If having username AND authentication does not exist in the context:
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Loading the full user by its username from the DB
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // Checking if token is valid for the respecting user
      if (jwtService.isTokenValid(jwt, userDetails)) {

        // 1. extrage "sid"
        String sessionId = jwtService.extractClaim(jwt, claims -> claims.get("sid", String.class));

        // System.out.println("sessionId: " + sessionId);

        // 2. verifica daca sesiunea e valida si activa
        Optional<UserSession> validSession = sessionService.validateAndRefresh(sessionId);

        if (validSession.isEmpty()) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write("{\"reason\":\"SESSION_EXPIRED\"}");
          return;
        }

        // 3. daca sesiunea e valida, autentifica userul
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // The respecting user is authenticated in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // Continuing with the "filterChain"
    filterChain.doFilter(request, response);
  }
}

