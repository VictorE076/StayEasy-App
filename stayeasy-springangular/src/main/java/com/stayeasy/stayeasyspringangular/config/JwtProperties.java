package com.stayeasy.stayeasyspringangular.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

  private String secret; // Secret key for signing JWT tokens.
  private long expiration; // Expiration time in ms.

}

