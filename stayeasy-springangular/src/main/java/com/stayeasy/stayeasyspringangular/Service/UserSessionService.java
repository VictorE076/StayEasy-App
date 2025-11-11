package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionService {

  private final UserSessionRepository sessionRepository;

  @Value("${app.session.timeout-minutes:15}")
  private int timeoutMinutes;

  public UserSession createSession(User user) {
    UserSession session = new UserSession();
    session.setUser(user);
    session.setCreatedAt(LocalDateTime.now());
    session.setLastActivity(LocalDateTime.now());
    session.setActive(true);
    return sessionRepository.save(session);
  }

  public boolean isSessionExpired(UserSession session) {
    LocalDateTime now = LocalDateTime.now();
    return Duration.between(session.getLastActivity(), now).toMinutes() > timeoutMinutes;
  }

  public Optional<UserSession> validateAndRefresh(String sessionId) {
    Optional<UserSession> sessionOpt = sessionRepository.findByIdAndActiveTrue(sessionId);
    if (sessionOpt.isEmpty()) return Optional.empty();

    UserSession session = sessionOpt.get();
    if (isSessionExpired(session)) {
      session.setActive(false);
      sessionRepository.save(session);
      return Optional.empty();
    }

    session.refreshActivity();
    sessionRepository.save(session);
    return Optional.of(session);
  }

  public void logout(String sessionId) {
    sessionRepository.findById(sessionId).ifPresent(session -> {
      session.setActive(false);
      sessionRepository.save(session);
    });
  }

}
