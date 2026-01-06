package com.stayeasy.stayeasyspringangular.Repository;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
  Optional<UserSession> findByIdAndActiveTrue(String sessionId);

  @Query("""
        SELECT new com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO(
            s.id,
            u.username,
            s.createdAt,
            s.lastActivity,
            s.active
        )
        FROM UserSession s
        JOIN s.user u
        ORDER BY s.createdAt DESC
    """)
  List<SessionAuditDTO> findAllSessionsForAudit();

}
