package com.stayeasy.stayeasyspringangular.registery;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
  private String fullName;
  public RegisterRequest(String username, String password, String email, String fullName) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.fullName = fullName;
  }
}
