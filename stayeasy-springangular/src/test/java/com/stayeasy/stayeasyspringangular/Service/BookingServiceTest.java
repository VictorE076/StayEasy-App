// java
package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.LoyaltyStatusDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private BookingService bookingService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void setAuthenticatedUser(String username) {
    Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  void createSimpleBooking_success() {
    setAuthenticatedUser("alice");
    User user = mock(User.class);
    Property property = mock(Property.class);

    when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    bookingService.createSimpleBooking(1L);

    verify(user, times(1)).addCompletedBooking();
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void createSimpleBooking_propertyNotFound_throwsResourceNotFound() {
    setAuthenticatedUser("bob");
    User user = mock(User.class);

    when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
    when(propertyRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookingService.createSimpleBooking(2L));
    verify(user, never()).addCompletedBooking();
    verify(userRepository, never()).save(any());
  }

  @Test
  void createSimpleBooking_unauthenticated_throwsUnauthorized() {
    // no authentication set
    assertThrows(UnauthorizedActionException.class, () -> bookingService.createSimpleBooking(1L));
    verifyNoInteractions(userRepository, propertyRepository);
  }

  @Test
  void createBookingWithDiscount_notEnoughCoins_throwsBadRequest() {
    setAuthenticatedUser("carol");
    User user = mock(User.class);
    Property property = mock(Property.class);

    when(userRepository.findByUsername("carol")).thenReturn(Optional.of(user));
    when(propertyRepository.findById(3L)).thenReturn(Optional.of(property));
    when(user.canUseDiscount()).thenReturn(false);

    assertThrows(BadRequestException.class, () -> bookingService.createBookingWithDiscount(3L));
    verify(user, never()).useDiscount();
    verify(user, never()).addCompletedBooking();
    verify(userRepository, never()).save(any());
  }

  @Test
  void createBookingWithDiscount_success() {
    setAuthenticatedUser("dave");
    User user = mock(User.class);
    Property property = mock(Property.class);

    when(userRepository.findByUsername("dave")).thenReturn(Optional.of(user));
    when(propertyRepository.findById(4L)).thenReturn(Optional.of(property));
    when(user.canUseDiscount()).thenReturn(true);

    bookingService.createBookingWithDiscount(4L);

    verify(user, times(1)).useDiscount();
    verify(user, times(1)).addCompletedBooking();
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void getMyLoyaltyStatus_returnsCorrectDto() {
    setAuthenticatedUser("ellen");
    User user = mock(User.class);

    when(userRepository.findByUsername("ellen")).thenReturn(Optional.of(user));
    when(user.getCompletedBookings()).thenReturn(7);
    when(user.getLoyaltyCoins()).thenReturn(2);
    when(user.bookingsUntilNextCoin()).thenReturn(3);

    LoyaltyStatusDTO dto = bookingService.getMyLoyaltyStatus();

    assertNotNull(dto);
    assertEquals(7, dto.getCompletedBookings());
    assertEquals(2, dto.getLoyaltyCoins());
  }
}
