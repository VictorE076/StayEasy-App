package com.stayeasy.stayeasyspringangular.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {

  private String username;
  private String password;

  public AuthRequest() {
  }

  public AuthRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

}

