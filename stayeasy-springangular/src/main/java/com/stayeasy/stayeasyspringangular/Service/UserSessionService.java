package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.UserSession;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class UserSessionService {

  private final UserSessionRepository sessionRepository;

  // Up to 15-20 minutes in production!
  // Up to 2 minutes in development or testing phase!
  @Value("${app.session.timeout-minutes:15}")
  private int timeoutMinutes;

  public UserSession createSession(User user, boolean rememberMe) {
    UserSession session = new UserSession();
    session.setUser(user);
    session.setCreatedAt(LocalDateTime.now());
    session.setLastActivity(LocalDateTime.now());
    session.setActive(true);
    session.setRememberMe(rememberMe);

    return sessionRepository.save(session);
  }

  public UserSession createSession(User user) {
    return createSession(user, false);
  }

  public boolean isSessionExpired(@NotNull UserSession session) {
    LocalDateTime now = LocalDateTime.now();

    // RememberMe Session expires after 14 days.
    // Normal Session expires after 15 minutes of inactivity.
    long effectiveTimeoutMinutes = session.isRememberMe()
      ? 14L * 24L * 60L
      : timeoutMinutes;

    return Duration.between(session.getLastActivity(), now).toMinutes() > effectiveTimeoutMinutes;
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

//  public boolean validateAndRefresh(String sessionId) {
//    UserSession session = sessionRepository.findById(sessionId)
//      .orElse(null);
//
//    if (session == null || !session.isActive()) {
//      return false;
//    }
//
//    LocalDateTime now = LocalDateTime.now();
//
//    // RememberMe Session expires after 14 days.
//    // Normal Session expires after 15 minutes of inactivity.
//    long timeoutMinutes = session.isRememberMe()
//      ? 14L * 24L * 60L
//      : 15L;
//
//    if (session.getLastActivity().plusMinutes(timeoutMinutes).isBefore(now)) {
//      session.setActive(false);
//      sessionRepository.save(session);
//      return false;
//    }
//
//    session.setLastActivity(now);
//    sessionRepository.save(session);
//
//    return true;
//  }

  public void logout(String sessionId) {
    UserSession session = sessionRepository.findById(sessionId)
      .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

    if (!session.isActive()) {
      throw new BadRequestException("Session is already closed");
    }

    session.setActive(false);
    sessionRepository.save(session);
  }

}
