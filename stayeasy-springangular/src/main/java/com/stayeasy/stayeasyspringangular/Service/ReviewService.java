package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.ReviewRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewResponseDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewSummaryDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.ReviewRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayeasy.stayeasyspringangular.exception.ForbiddenActionException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReviewService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final PropertyRepository propertyRepository;

  @Transactional(readOnly = true)
  public List<ReviewResponseDTO> listForProperty(Long propertyId) {

    // Throws 404 if property is missing
    propertyRepository.findById(propertyId)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    return reviewRepository.findByProperty_IdOrderByCreatedAtDesc(propertyId)
      .stream()
      .map(this::mapToResponse)
      .toList();

  }


  // CREATE or UPDATE the current user's review for a property ( 1 review / user / property ).
  @Transactional
  public ReviewResponseDTO createOrUpdateReview(Long propertyId, ReviewRequestDTO dto) {

    User currentUser = getCurrentUser();

    Property property = propertyRepository.findById(propertyId)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    // Reviewing your own property is FORBIDDEN
    if (property.getOwner() != null && property.getOwner().getId() != null
      && property.getOwner().getId().equals(currentUser.getId())) {

      logger.warn("Review denied: user id {} attempted to review own property id {}", currentUser.getId(), propertyId);

      throw new ForbiddenActionException("You cannot review your own property");
    }

    // CREATE one review per user per property ( UPDATE if already exists ).
    Review review = reviewRepository.findByProperty_IdAndUser_Id(propertyId, currentUser.getId())
      .orElseGet(Review::new);

    boolean isNew = (review.getId() == null);

    review.setUser(currentUser);
    review.setProperty(property);
    review.setRating(dto.getRating());
    review.setComment(dto.getComment());

    // Set "createdAt" once
    if (isNew) {
      review.setCreatedAt(LocalDateTime.now());
    }

    Review savedReview = reviewRepository.save(review);

    logger.info("{} review id {} for property id {} by user id {}", isNew ? "Created" : "Updated", savedReview.getId(), propertyId, currentUser.getId());

    return mapToResponse(savedReview);

  }

  // DELETE review only if the current logged-in user is the author (except for "ROLE_ADMIN").
  @Transactional
  public void deleteReview(Long reviewId) {

    User currentUser = getCurrentUser();

    Review review = reviewRepository.findById(reviewId)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

    boolean isAdmin = getAuthenticationContext().getAuthorities().stream()
      .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

    if(!isAdmin) { // Only normal (GUEST or HOST) user must also be the review's owner.
      if (review.getUser() == null || review.getUser().getId() == null
        || !review.getUser().getId().equals(currentUser.getId())) {

        logger.warn("Review delete denied. Review id {}, current user id {}", reviewId, currentUser.getId());

        throw new ForbiddenActionException("You are not allowed to delete this review");
      }
    }

    reviewRepository.delete(review);

    logger.info("Review id {} deleted successfully by user id {}", reviewId, currentUser.getId());

  }


  // Returns average rating + count for a property ( Throws 404 if property missing ).
  @Transactional(readOnly = true)
  public ReviewSummaryDTO summary(Long propertyId) {

    propertyRepository.findById(propertyId)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    Double avg = reviewRepository.averageRating(propertyId);
    Long count = reviewRepository.countByProperty(propertyId);

    return ReviewSummaryDTO.builder()
      .propertyId(propertyId)
      .averageRating(avg == null ? 0.0 : avg)
      .reviewsCount(count == null ? 0 : count)
      .build();

  }

  private ReviewResponseDTO mapToResponse(Review r) {

    return ReviewResponseDTO.builder()
      .id(r.getId())
      .propertyId(r.getProperty() == null ? null : r.getProperty().getId())
      .rating(r.getRating())
      .comment(r.getComment())
      .username(r.getUser() == null ? null : r.getUser().getUsername())
      .createdAt(r.getCreatedAt())
      .build();

  }


  // Auth helpers
  private Authentication getAuthenticationContext() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private User getCurrentUser() {
    Authentication auth = getAuthenticationContext();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedActionException("You must be logged in");
    }

    String username = auth.getName();
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UnauthorizedActionException("Authenticated user was not found"));
  }

}
