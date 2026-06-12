package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminAuditService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(AdminAuditService.class);

  private final UserSessionRepository userSessionRepository;

  public AdminAuditService(UserSessionRepository userSessionRepository) {
    this.userSessionRepository = userSessionRepository;
  }

  public List<SessionAuditDTO> getAllUserSessions() {
    List<SessionAuditDTO> sessions = userSessionRepository.findAllSessionsForAudit();

    logger.info("Admin audit loaded {} user sessions", sessions.size());

    return sessions;
  }

}
