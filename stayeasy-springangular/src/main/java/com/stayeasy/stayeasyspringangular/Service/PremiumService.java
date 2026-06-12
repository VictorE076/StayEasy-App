package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.PremiumStatusDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PremiumService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(PremiumService.class);

  private final UserRepository userRepository;

  public PremiumStatusDTO getPremiumStatus() {
    User user = getCurrentUser();

    logger.debug("Premium status requested for user id {}", user.getId());

    return mapToPremiumStatus(user);
  }

  @Transactional
  public PremiumStatusDTO activateDemoPremium() {
    User user = getCurrentUser();
    user.setPremium(true);
    userRepository.save(user);

    logger.info("Premium activated for user id {}", user.getId());

    return mapToPremiumStatus(user);
  }

  @Transactional
  public PremiumStatusDTO deactivateDemoPremium() {
    User user = getCurrentUser();
    user.setPremium(false);
    userRepository.save(user);

    logger.info("Premium deactivated for user id {}", user.getId());

    return mapToPremiumStatus(user);
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedActionException("You must be logged in");
    }

    String username = auth.getName();

    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UnauthorizedActionException("Authenticated user was not found"));
  }

  private PremiumStatusDTO mapToPremiumStatus(User user) {
    if (user.isPremium()) {
      return new PremiumStatusDTO(
        true,
        "StayEasy Premium",
        "9.99 $/month",
        "Premium account is active. You receive 2x StayEasy Coins and access to member-only benefits."
      );
    }

    return new PremiumStatusDTO(
      false,
      "Basic Account",
      "Free",
      "Upgrade to StayEasy Premium to receive 2x StayEasy Coins and access to exclusive member benefits."
    );
  }
}
