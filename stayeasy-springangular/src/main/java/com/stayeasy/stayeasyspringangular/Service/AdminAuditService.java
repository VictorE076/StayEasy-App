package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAuditService {

  private final UserSessionRepository userSessionRepository;

  public AdminAuditService(UserSessionRepository userSessionRepository) {
    this.userSessionRepository = userSessionRepository;
  }

  public List<SessionAuditDTO> getAllUserSessions() {
    return userSessionRepository.findAllSessionsForAudit();
  }

}
