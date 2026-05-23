package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.LoyaltyStatusDTO;
import com.stayeasy.stayeasyspringangular.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  @PostMapping("/book-now/{propertyId}")
  public ResponseEntity<String> bookNow(@PathVariable Long propertyId) {
    bookingService.createSimpleBooking(propertyId);
    return ResponseEntity.ok("Booking successful!");
  }

  @GetMapping("/my-loyalty")
  public ResponseEntity<LoyaltyStatusDTO> getMyLoyaltyStatus() {
    return ResponseEntity.ok(bookingService.getMyLoyaltyStatus());
  }
}
