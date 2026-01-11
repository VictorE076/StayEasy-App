package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.ReviewRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewResponseDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewSummaryDTO;
import com.stayeasy.stayeasyspringangular.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

  private final ReviewService reviewService;

  @GetMapping("/properties/{propertyId}/reviews")
  public List<ReviewResponseDTO> listForProperty(@PathVariable Long propertyId) {
    return reviewService.listForProperty(propertyId);
  }

  // Upsert: PUT (idempotent) -> Create or replace the review at the exact address.
  @PutMapping("/properties/{propertyId}/reviews")
  public ReviewResponseDTO createOrUpdate(
    @PathVariable Long propertyId,
    @Valid @RequestBody ReviewRequestDTO dto
  ) {
    return reviewService.createOrUpdateReview(propertyId, dto);
  }

  @DeleteMapping("/reviews/{reviewId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long reviewId) {
    reviewService.deleteReview(reviewId);
  }

  @GetMapping("/properties/{propertyId}/reviews/summary")
  public ReviewSummaryDTO summary(@PathVariable Long propertyId) {
    return reviewService.summary(propertyId);
  }
}

