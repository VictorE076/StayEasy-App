package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_sessions")
public class UserSession {

  //  Getters & Setters
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id; // va fi "sid" in JWT

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Setter
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Setter
  @Column(nullable = false)
  private LocalDateTime lastActivity;

  @Setter
  @Column(nullable = false)
  private boolean active;

  // Constructors
  public UserSession() {}

  // constructor cu parametri
  public UserSession(User user, LocalDateTime createdAt, LocalDateTime lastActivity, boolean active) {
    this.user = user;
    this.createdAt = createdAt;
    this.lastActivity = lastActivity;
    this.active = active;
  }

  public void refreshActivity() { this.lastActivity = LocalDateTime.now(); }

}

