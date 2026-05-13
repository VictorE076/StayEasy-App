package com.stayeasy.stayeasyspringangular.registery;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must have between 3 and 50 characters")
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must have at least 6 characters")
  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email address")
  private String email;

  @NotBlank(message = "Full name is required")
  @Size(max = 100, message = "Full name must have at most 100 characters")
  private String fullName;

  public RegisterRequest(String username, String password, String email, String fullName) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.fullName = fullName;
  }
}
