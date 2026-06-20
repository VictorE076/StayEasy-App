package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.PremiumStatusDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @InjectMocks
  private PremiumService premiumService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();

//    testUser.setId(1L);
    ReflectionTestUtils.setField(testUser, "id", 1);

    testUser.setUsername("testuser");
    testUser.setPremium(false);

    SecurityContextHolder.setContext(securityContext);
    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.isAuthenticated()).thenReturn(true);
    lenient().when(authentication.getName()).thenReturn("testuser");
    lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
  }

  @Test
  void testGetPremiumStatusForBasicUser() {
    PremiumStatusDTO result = premiumService.getPremiumStatus();

    assertNotNull(result);
    assertFalse(result.premium());
    assertEquals("Basic Account", result.planName());
    assertEquals("Free", result.price());
    verify(userRepository).findByUsername("testuser");
  }

  @Test
  void testGetPremiumStatusForPremiumUser() {
    testUser.setPremium(true);

    PremiumStatusDTO result = premiumService.getPremiumStatus();

    assertNotNull(result);
    assertTrue(result.premium());
    assertEquals("StayEasy Premium", result.planName());
    assertEquals("9.99 $/month", result.price());
  }

  @Test
  void testActivateDemoPremium() {
    PremiumStatusDTO result = premiumService.activateDemoPremium();

    assertNotNull(result);
    assertTrue(result.premium());
    assertEquals("StayEasy Premium", result.planName());
    verify(userRepository).save(testUser);
  }

  @Test
  void testDeactivateDemoPremium() {
    testUser.setPremium(true);

    PremiumStatusDTO result = premiumService.deactivateDemoPremium();

    assertNotNull(result);
    assertFalse(result.premium());
    assertEquals("Basic Account", result.planName());
    verify(userRepository).save(testUser);
  }

  @Test
  void testGetPremiumStatusUnauthorized() {
    when(authentication.isAuthenticated()).thenReturn(false);

    assertThrows(UnauthorizedActionException.class, () -> premiumService.getPremiumStatus());
  }

  @Test
  void testGetPremiumStatusUserNotFound() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    assertThrows(UnauthorizedActionException.class, () -> premiumService.getPremiumStatus());
  }

  @Test
  void testGetPremiumStatusNullAuthentication() {
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(UnauthorizedActionException.class, () -> premiumService.getPremiumStatus());
  }
}
