package com.stayeasy.stayeasyspringangular.registery;

public class RegisterRequest {
  private String Username;
  private String Password;
  private String Email;
  private String fullName;
  public RegisterRequest(String username, String password, String email, String fullName) {
    this.Username = username;
    this.Password = password;
    this.Email = email;
    this.fullName = fullName;
  }
 public String getUsername() {
    return Username;
 }
 public void setUsername(String username) {
    this.Username = username;
 }
 public String getPassword() {
    return Password;
 }
 public void setPassword(String password) {
    this.Password = password;
 }
 public String getEmail() {
    return Email;
 }
 public void setEmail(String email) {
    this.Email = email;
 }
 public String getfullName() {
    return fullName;
 }
 public void setfullName(String fullName) {
    this.fullName = fullName;
 }
}
