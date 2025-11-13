package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // <- important, numele exact din DB
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id; // corespunde coloanei id INT AUTO_INCREMENT

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.GUEST; // default 'user'

  // Relație 1-to-many cu sesiunile (pe care tu o vei crea)
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserSession> sessions = new ArrayList<>();

  // === Constructors ===
  public User() {}

  public User(String username, String email, String passwordHash, String fullName, Role role) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.fullName = fullName;
    this.role = role;
    this.createdAt = LocalDateTime.now();
  }

  // === Getters & Setters ===
  public Integer getId() { return id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public List<UserSession> getSessions() { return sessions; }
  public void setSessions(List<UserSession> sessions) { this.sessions = sessions; }
}

