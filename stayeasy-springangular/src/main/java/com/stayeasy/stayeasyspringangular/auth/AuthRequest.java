package com.stayeasy.stayeasyspringangular.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  private boolean rememberMe;

  public AuthRequest() {
  }

  public AuthRequest(String username, String password) {
      this.username = username;
      this.password = password;
      this.rememberMe = false;
  }

  public AuthRequest(String username, String password, boolean rememberMe) {
      this.username = username;
      this.password = password;
      this.rememberMe = rememberMe;
  }
}
