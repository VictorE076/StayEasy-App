package com.stayeasy.stayeasyspringangular.security;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  private final JwtProperties jwtProperties;

  public JwtService(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  // Extracting username (subject) from token.
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extracting expiration date from token.
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Extracting any type of claim from token using a "resolver".
  public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Generating a simple token without extra claims (for a certain user).
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  // Generating a JWT token with extra claims.
  public String generateToken(Map<String, Object> extraClaims, @NotNull UserDetails userDetails) {
    long expirationMillis = jwtProperties.getExpiration();

    Date now = new Date(System.currentTimeMillis());
    Date expirationDate = new Date(System.currentTimeMillis() + expirationMillis);

    return Jwts.builder()
      .setClaims(extraClaims)
      .setSubject(userDetails.getUsername())              // subject = username
      .setIssuedAt(now)                                   // generating moment = now
      .setExpiration(expirationDate)                      // when token expires
      .signWith(getSignInKey(), SignatureAlgorithm.HS256) // digital signature HMAC-SHA256
      .compact();
  }

  // Checking if the given token is valid for the given user.
  public boolean isTokenValid(String token, @NotNull UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  // Checking if the given token has already expired.
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Parsing the token & obtaining all claims (username, expiration, etc.).
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSignInKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  // Building the signing key starting from application.properties Secret.
  private @NotNull Key getSignInKey() {
    String secret = jwtProperties.getSecret();
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(@NotNull User user, String sessionId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sid", sessionId);
    claims.put("role", "ROLE_" + user.getRole().name());
    claims.put("userId", user.getId());
    return Jwts.builder()
      .setClaims(claims)
      .setSubject(user.getUsername())
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h default exp
      .signWith(getSignInKey(), SignatureAlgorithm.HS256)
      .compact();
  }

}

