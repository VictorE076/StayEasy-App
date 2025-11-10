package com.stayeasy.stayeasyspringangular.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

  private String secret; // Secret key for signing JWT tokens.
  private long expiration; // Expiration time in ms.

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getExpiration() {
    return expiration;
  }

  public void setExpiration(long expiration) {
    this.expiration = expiration;
  }
}

