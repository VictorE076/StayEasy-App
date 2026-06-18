package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.ReviewRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewResponseDTO;
import com.stayeasy.stayeasyspringangular.DTO.ReviewSummaryDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.ReviewRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.stayeasy.stayeasyspringangular.exception.ForbiddenActionException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private ReviewService reviewService;

  @BeforeEach
  void clearContext() {SecurityContextHolder.clearContext();}

  // helper pentru authentication
  private void mockAuth(String username) {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(username);
//    when(auth.getAuthorities()).thenReturn(List.of()); // unnecessary since we don't check authorities in the service

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private User createUser(Integer id , String username, Role role) {
    User user = new User();

//    user.setId(id);
    ReflectionTestUtils.setField(user, "id", id);

    user.setUsername(username);
    user.setRole(role);
    return user;
  }

  private Property createProperty(Long id, User owner) {
    Property property = new Property();

//    property.setId(1L);
    ReflectionTestUtils.setField(property, "id", id);

    property.setTitle("Test");
    property.setCity("Bucharest");
    property.setPricePerNight(BigDecimal.valueOf(100));
    property.setOwner(owner);
    return property;
  }

  private Review createReview(Long id, User user, Property property) {
    Review review = new Review();

    ReflectionTestUtils.setField(review, "id", id);

    review.setUser(user);
    review.setProperty(property);
    review.setRating(5);
    review.setComment("Excelent!");
    review.setCreatedAt(LocalDateTime.now());

    return review;
  }

//  list for property

  @Test
  void listForProperty_shouldReturnReviews() {

    Property property = new Property();

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    when(reviewRepository.findByProperty_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(new Review()));

    List<ReviewResponseDTO> result = reviewService.listForProperty(1L);

    assertEquals(1, result.size());
  }

  @Test
  void listForProperty_shouldThrowIfPropertyNotFound() {

    when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
      () -> reviewService.listForProperty(1L));

  }

//  create review

  @Test
  void createReview_shouldCreateNewReview() {

    User user = createUser(1, "user1", Role.GUEST);
    Property property = createProperty(1L, null);

    mockAuth("user1");

    ReviewRequestDTO dto = new ReviewRequestDTO();
    dto.setRating(5);
    dto.setComment("Excelent!");

    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    when(reviewRepository.findByProperty_IdAndUser_Id(1L, 1)).thenReturn(Optional.empty());

    when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ReviewResponseDTO result = reviewService.createOrUpdateReview(1L, dto);

    assertNotNull(result);
    assertEquals(5, result.getRating());

    verify(reviewRepository).save(any());
  }

  @Test
  void createReview_shouldUpdateExistingReview() {

    User user = createUser(1, "user1", Role.GUEST);
    Property property = createProperty(1L, null);
    Review existingReview = createReview(1L, user, property);

    mockAuth("user1");

    ReviewRequestDTO dto = new ReviewRequestDTO();
    dto.setRating(4);
    dto.setComment("Good!");

    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    when(reviewRepository.findByProperty_IdAndUser_Id(1L, 1)).thenReturn(Optional.of(existingReview));

    when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ReviewResponseDTO result = reviewService.createOrUpdateReview(1L, dto);

    assertNotNull(result);
    assertEquals(4, result.getRating());
    assertEquals("Good!", result.getComment());

    verify(reviewRepository).save(any());
  }

  @Test
  void createReview_shouldFailIfUserReviewsOwnProperty() {

    User owner = createUser(1, "owner", Role.HOST);
    Property property = createProperty(1L, owner);

    mockAuth("owner");

    ReviewRequestDTO dto = new ReviewRequestDTO();
    dto.setRating(5);
    dto.setComment("Awesome!");

    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    assertThrows(ForbiddenActionException.class,
      () -> reviewService.createOrUpdateReview(1L, dto));

  }

//  delete review

  @Test
  void deleteReview_shouldWorkForOwner() {

    User user = createUser(1, "user1", Role.GUEST);
    Property property = createProperty(1L, null);
    Review review = createReview(1L, user, property);

    mockAuth("user1");

    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

    reviewService.deleteReview(1L);

    verify(reviewRepository).delete(review);
  }

  @Test
  void deleteReview_shouldFailIfNotOwner() {

    User user = createUser(1, "user1", Role.GUEST);
    mockAuth("user1");

    User owner = createUser(2, "owner", Role.HOST);

    Property property = createProperty(1L, owner);

    Review review = createReview(1L, owner, property);

    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

    assertThrows(ForbiddenActionException.class,
      () -> reviewService.deleteReview(1L));
  }

  @Test
  void deleteReview_shouldThrowIfReviewMissing() {

    User user = createUser(1, "user1", Role.GUEST);

    mockAuth("user1");

    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

    when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
      () -> reviewService.deleteReview(1L));

  }

//  summary

  @Test
  void summary_shouldReturnCorrectData() {

    Property property = new Property();

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    when(reviewRepository.averageRating(1L)).thenReturn(4.5);

    when(reviewRepository.countByProperty(1L)).thenReturn(10L);

    ReviewSummaryDTO result = reviewService.summary(1L);

    assertEquals(4.5, result.getAverageRating());
    assertEquals(10L, result.getReviewsCount());
  }

  @Test
  void summary_shouldReturnZeroIfNoReviews() {

    Property property = new Property();

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    when(reviewRepository.averageRating(1L)).thenReturn(null);

    when(reviewRepository.countByProperty(1L)).thenReturn(null);

    ReviewSummaryDTO result = reviewService.summary(1L);

    assertEquals(0.0, result.getAverageRating());
    assertEquals(0L, result.getReviewsCount());
  }

  @Test
  void summary_shouldThrowIfPropertyMissing() {

    when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
      () -> reviewService.summary(1L));

  }

}
