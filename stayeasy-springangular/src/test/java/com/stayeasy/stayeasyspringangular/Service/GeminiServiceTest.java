package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private RestTemplate restTemplate;

  private GeminiService createServiceWithMocks() {
    GeminiService service = new GeminiService(propertyRepository);
    // Set private fields
    ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
    ReflectionTestUtils.setField(service, "apiKey", "?api_key=test");
    ReflectionTestUtils.setField(service, "apiUrl", "https://api.gemini/");
    return service;
  }

  @Test
  void summarizeReviews_propertyNotFound_throwsResourceNotFound() {
    GeminiService service = createServiceWithMocks();

    when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.summarizeReviews(1L));
    verify(propertyRepository).findById(1L);
  }

  @Test
  void summarizeReviews_noReviews_returnsNoReviewsMessage() {
    GeminiService service = createServiceWithMocks();

    Property property = mock(Property.class);
    when(property.getReviews()).thenReturn(Collections.emptyList());
    when(propertyRepository.findById(2L)).thenReturn(Optional.of(property));

    String result = service.summarizeReviews(2L);

    assertEquals("This property does not have any reviews to summarize yet.", result);
    verify(propertyRepository).findById(2L);
  }

  @Test
  void summarizeReviews_successfulResponse_returnsSummaryText() {
    GeminiService service = createServiceWithMocks();

    // Prepare property with one review
    Property property = mock(Property.class);
    Review review = mock(Review.class);
    when(review.getComment()).thenReturn("Nice stay");
    when(review.getRating()).thenReturn(5);
    when(property.getReviews()).thenReturn(List.of(review));
    when(propertyRepository.findById(3L)).thenReturn(Optional.of(property));

    // Prepare Gemini-like response structure
    Map<String, Object> part = new HashMap<>();
    part.put("text", "Short summary from AI.");

    Map<String, Object> content = new HashMap<>();
    content.put("parts", List.of(part));

    Map<String, Object> candidate = new HashMap<>();
    candidate.put("content", content);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("candidates", List.of(candidate));

    String expectedUrl = "https://api.gemini/?api_key=test";

    when(restTemplate.postForObject(eq(expectedUrl), any(), eq(Map.class))).thenReturn(responseMap);

    String result = service.summarizeReviews(3L);

    assertEquals("Short summary from AI.", result);
    verify(restTemplate).postForObject(eq(expectedUrl), any(), eq(Map.class));
  }

  @Test
  void summarizeReviews_noCandidates_returnsNoCandidatesMessage() {
    GeminiService service = createServiceWithMocks();

    Property property = mock(Property.class);
    Review review = mock(Review.class);
    when(review.getComment()).thenReturn("Ok");
    when(review.getRating()).thenReturn(4);
    when(property.getReviews()).thenReturn(List.of(review));
    when(propertyRepository.findById(4L)).thenReturn(Optional.of(property));

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("candidates", Collections.emptyList());

    String expectedUrl = "https://api.gemini/?api_key=test";
    when(restTemplate.postForObject(eq(expectedUrl), any(), eq(Map.class))).thenReturn(responseMap);

    String result = service.summarizeReviews(4L);

    assertEquals("No candidates were found in the Gemini response.", result);
  }

  @Test
  void summarizeReviews_restClientError_returnsUnavailableMessage() {
    GeminiService service = createServiceWithMocks();

    Property property = mock(Property.class);
    Review review = mock(Review.class);
    when(review.getComment()).thenReturn("Good");
    when(review.getRating()).thenReturn(5);
    when(property.getReviews()).thenReturn(List.of(review));
    when(propertyRepository.findById(5L)).thenReturn(Optional.of(property));

    String expectedUrl = "https://api.gemini/?api_key=test";
    when(restTemplate.postForObject(eq(expectedUrl), any(), eq(Map.class)))
      .thenThrow(new RestClientResponseException("err", 500, "Internal", null, null, null));

    String result = service.summarizeReviews(5L);

    assertEquals("AI summary is temporarily unavailable. Please try again later.", result);
  }

  @Test
  void summarizeReviews_unexpectedException_returnsErrorMessage() {
    GeminiService service = createServiceWithMocks();

    Property property = mock(Property.class);
    Review review = mock(Review.class);
    when(review.getComment()).thenReturn("Bad");
    when(review.getRating()).thenReturn(1);
    when(property.getReviews()).thenReturn(List.of(review));
    when(propertyRepository.findById(6L)).thenReturn(Optional.of(property));

    String expectedUrl = "https://api.gemini/?api_key=test";
    when(restTemplate.postForObject(eq(expectedUrl), any(), eq(Map.class)))
      .thenThrow(new RuntimeException("boom"));

    String result = service.summarizeReviews(6L);

    assertEquals("Error while processing the AI summary. Please try again later.", result);
  }
}
