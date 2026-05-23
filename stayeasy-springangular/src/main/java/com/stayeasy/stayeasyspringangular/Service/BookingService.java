package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final UserRepository userRepository;
  private final PropertyRepository propertyRepository;

  @Transactional
  public void createSimpleBooking(Long propertyId) {
    // 1. Luam user logat
    User currentUser = getCurrentUser();

    // 2. Verificam dacă proprietatea exista
    Property property = propertyRepository.findById(propertyId)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    // 3. Adaugam logica de loialitate
    currentUser.addCompletedBooking();

    // 4. Salvam user-ul cu noile puncte
    userRepository.save(currentUser);

    System.out.println("Am adaugat o rezervare pentru user " + currentUser.getUsername() + ". Total completed bookings: " + currentUser.getCompletedBookings() + ", Loyalty coins: " + currentUser.getLoyaltyCoins());

  }

  // Helper pentru a lua user logat
  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedActionException("You must be logged in");
    }

    String username = auth.getName();
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UnauthorizedActionException("Authenticated user was not found"));
  }
}
