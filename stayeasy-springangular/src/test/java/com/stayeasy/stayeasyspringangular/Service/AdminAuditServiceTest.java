package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.stayeasy.stayeasyspringangular.DTO.PageResponseDTO;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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


  // PAGINATION

  @Test
  void getAllUserSessionsPaged_validRequest_shouldReturnPageResponse() {
    SessionAuditDTO session = new SessionAuditDTO(
      "session1",
      "user1",
      LocalDateTime.now(),
      LocalDateTime.now(),
      true
    );

    when(userSessionRepository.findAllSessionsForAudit(any(org.springframework.data.domain.Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(session)));

    PageResponseDTO<SessionAuditDTO> result =
      adminAuditService.getAllUserSessionsPaged(0, 5, "createdAt", "desc");

    assertEquals(1, result.getContent().size());
    assertEquals(0, result.getPageNumber());
    assertEquals("createdAt", result.getSortBy());
    assertEquals("desc", result.getDirection());
  }

  @Test
  void getAllUserSessionsPaged_negativePage_shouldThrowBadRequest() {
    assertThrows(BadRequestException.class,
      () -> adminAuditService.getAllUserSessionsPaged(-1, 5, "createdAt", "desc"));

    verify(userSessionRepository, never())
      .findAllSessionsForAudit(any(org.springframework.data.domain.Pageable.class));
  }

  @Test
  void getAllUserSessionsPaged_invalidSize_shouldThrowBadRequest() {
    assertThrows(BadRequestException.class,
      () -> adminAuditService.getAllUserSessionsPaged(0, 0, "createdAt", "desc"));

    verify(userSessionRepository, never())
      .findAllSessionsForAudit(any(org.springframework.data.domain.Pageable.class));
  }

}
