package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuditServiceTest {

  @Mock
  private UserSessionRepository userSessionRepository;

  @InjectMocks
  private AdminAuditService adminAuditService;

//  get all user sessions

  @Test
  void getAllUserSessions_shouldReturnSessions() {

    SessionAuditDTO session1 = new SessionAuditDTO(
      "session1",
      "user1",
      LocalDateTime.now(),
      LocalDateTime.now(),
      true);

    SessionAuditDTO session2 = new SessionAuditDTO(
      "session2",
      "user2",
      LocalDateTime.now(),
      LocalDateTime.now(),
      false);

    when(userSessionRepository.findAllSessionsForAudit()).thenReturn(List.of(session1, session2));

    List<SessionAuditDTO> result = adminAuditService.getAllUserSessions();

    assertNotNull(result);

    assertEquals(2, result.size());

    assertEquals("user1", result.get(0).username());
    assertEquals("user2", result.get(1).username());

    verify(userSessionRepository).findAllSessionsForAudit();
  }

  @Test
  void getAllUserSessions_shouldReturnEmptyList() {

    when(userSessionRepository.findAllSessionsForAudit()).thenReturn(List.of());

    List<SessionAuditDTO> result = adminAuditService.getAllUserSessions();

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(userSessionRepository).findAllSessionsForAudit();
  }
}
