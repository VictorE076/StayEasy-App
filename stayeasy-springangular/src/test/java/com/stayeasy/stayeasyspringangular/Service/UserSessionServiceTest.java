package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

  @Mock
  private UserSessionRepository userSessionRepository;

  @InjectMocks
  private UserSessionService userSessionService;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(userSessionService, "timeoutMinutes", 15);
  }

  private User createUser() {
    User user = new User();

    user.setUsername("test_user");
    user.setEmail("mail_proba@test.com");
    user.setPasswordHash("123456789");
    user.setRole(Role.GUEST);

    return user;
  }

  private UserSession createUserSession() {
    UserSession userSession = new UserSession();

    userSession.setActive(true);
    userSession.setCreatedAt(LocalDateTime.now());
    userSession.setLastActivity(LocalDateTime.now());

    return userSession;
  }

//  create session

  @Test
  void createSession_shouldCreateSession() {
    User user = createUser();

    when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> i.getArgument(0));

    UserSession result = userSessionService.createSession(user);

    assertNotNull(result);
    assertTrue(result.isActive());
    assertEquals(user, result.getUser());

    verify(userSessionRepository).save(any(UserSession.class));
  }

//  session expired

  @Test
  void isSessionExpired_shouldReturnTrue() {
    UserSession session = createUserSession();

    session.setLastActivity(LocalDateTime.now().minusMinutes(20));

    boolean result = userSessionService.isSessionExpired(session);

    assertTrue(result);
  }

  @Test
  void isSessionExpired_shouldReturnFalse() {
    UserSession session = createUserSession();

    session.setLastActivity(LocalDateTime.now().minusMinutes(5));

    boolean result = userSessionService.isSessionExpired(session);

    assertFalse(result);
  }

//  validate and refresh

  @Test
  void validateAndRefresh_shouldReturnEmptyIfSessionMissing() {
    when(userSessionRepository.findByIdAndActiveTrue("abc")).thenReturn(Optional.empty());

    Optional<UserSession> result = userSessionService.validateAndRefresh("abc");

    assertTrue(result.isEmpty());
  }

  @Test
  void validateAndRefresh_shouldExpireSession() {
    UserSession session = createUserSession();
    session.setLastActivity(LocalDateTime.now().minusMinutes(20));

    when(userSessionRepository.findByIdAndActiveTrue("abc")).thenReturn(Optional.of(session));

    when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> i.getArgument(0));

    Optional<UserSession> result = userSessionService.validateAndRefresh("abc");

    assertTrue(result.isEmpty());
    assertFalse(session.isActive());
    verify(userSessionRepository).save(session);
  }

  @Test
  void validateAndRefresh_shouldRefreshSession() {
    UserSession session = createUserSession();
    session.setLastActivity(LocalDateTime.now().minusMinutes(2));

    when(userSessionRepository.findByIdAndActiveTrue("abc")).thenReturn(Optional.of(session));

    when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> i.getArgument(0));

    Optional<UserSession> result = userSessionService.validateAndRefresh("abc");

    assertTrue(result.isPresent());
    assertTrue(result.get().isActive());
    verify(userSessionRepository).save(session);
  }

//  logout

  @Test
  void logout_shouldDeactivateSession() {
    UserSession session = createUserSession();

    when(userSessionRepository.findById("abc")).thenReturn(Optional.of(session));

    when(userSessionRepository.save(any(UserSession.class))).thenAnswer(i -> i.getArgument(0));

    userSessionService.logout("abc");

    assertFalse(session.isActive());
    verify(userSessionRepository).save(session);
  }

  @Test
  void logout_shouldDoNothingIfSessionMissing() {
    when(userSessionRepository.findById("abc")).thenReturn(Optional.empty());

    userSessionService.logout("abc");

    verify(userSessionRepository, never()).save(any(UserSession.class));
  }
}
