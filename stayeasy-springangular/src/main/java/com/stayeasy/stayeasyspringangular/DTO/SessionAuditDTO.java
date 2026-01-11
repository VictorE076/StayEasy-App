package com.stayeasy.stayeasyspringangular.DTO;

import java.time.LocalDateTime;

public record SessionAuditDTO(
  String sessionId,
  String username,
  LocalDateTime createdAt,
  LocalDateTime lastActivity,
  boolean active
) {}
