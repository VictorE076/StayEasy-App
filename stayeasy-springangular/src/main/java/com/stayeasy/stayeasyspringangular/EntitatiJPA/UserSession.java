package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id; // va fi "sid" in JWT

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime lastActivity;

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

  //  Getters & Setters
  public String getId() { return id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getLastActivity() { return lastActivity; }
  public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public void refreshActivity() { this.lastActivity = LocalDateTime.now(); }

}

