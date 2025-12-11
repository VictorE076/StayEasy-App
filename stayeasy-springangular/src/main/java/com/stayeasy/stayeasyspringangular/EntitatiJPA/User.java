package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users") // <- important, numele exact din DB
public class User {

  // === Getters & Setters ===
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id; // corespunde coloanei id INT AUTO_INCREMENT

  @Setter
  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Setter
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Setter
  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Setter
  @Column(name = "full_name", length = 100)
  private String fullName;

  @Setter
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.GUEST; // default 'user'

  // Relație 1-to-many cu sesiunile (pe care tu o vei crea)
  @Setter
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

}

