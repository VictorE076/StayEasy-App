package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.ReviewResposeDTO;
import lombok.RequiredArgsConstructor;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.DTO.PropertyRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewRequestDTO;
import com.stayeasy.stayeasyspringangular.Repository.ReviewRepository;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewRepository reviewRepo;
  private final UserRepository userRepo;
  private final PropertyRepository propertyRepo;

  @PostMapping
  public ResponseEntity<Void> addReview(@RequestBody ReviewRequestDTO.ReviewRequest dto) {

    User user = userRepo.findByUsername(String.valueOf(dto.getUserName())).orElseThrow();
    Property property = propertyRepo.findById(dto.getPropertyId()).orElseThrow();

    Review review = Review.builder()
      .rating(dto.getRating())
      .comment(dto.getComment())
      .user(user)
      .property(property)
      .createdAt(LocalDateTime.now())
      .build();

    reviewRepo.save(review);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/property/{id}")
  public List<ReviewResposeDTO.ReviewResponse> getReviews(@PathVariable Long id) {
    return reviewRepo.findByPropertyId(id).stream()
      .map(r -> new ReviewResposeDTO.ReviewResponse(
        r.getRating(),
        r.getComment(),
        r.getUser().getUsername(),
        r.getCreatedAt()
      ))
      .toList();
  }
}
