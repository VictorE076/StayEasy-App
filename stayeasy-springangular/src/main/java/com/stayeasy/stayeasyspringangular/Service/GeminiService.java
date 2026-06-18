package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import org.springframework.web.client.RestClientResponseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeminiService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  private final PropertyRepository propertyRepository;
  private final RestTemplate restTemplate = new RestTemplate();

  public GeminiService(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  public String summarizeReviews(Long propertyId) {

    logger.info("Generating AI review summary for property id {}", propertyId);

    var property = propertyRepository.findById(propertyId)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    List<Review> reviews = property.getReviews();

    if (reviews == null || reviews.isEmpty()) {

      logger.info("No reviews available for AI summary on property id {}", propertyId);

      return "This property does not have any reviews to summarize yet.";
    }

    String allReviewsText = reviews.stream()
      .map(r -> "- " + r.getComment() + " (Rating: " + r.getRating() + "/5)")
      .collect(Collectors.joining("\n"));

    String prompt =
      "You are an intelligent assistant for StayEasy, a property booking platform. " +
        "Below is a list of guest reviews for a property. " +
        "Generate a short, objective summary in English, structured into 3 main ideas. " +
        "Use simple bullet points starting with '-', without Markdown formatting, without ** characters and without bold headings.\n\n" +
        "Reviews:\n" +
        allReviewsText;

    Map<String, Object> requestBody = Map.of(
      "contents", List.of(
        Map.of("parts", List.of(
          Map.of("text", prompt)
        ))
      )
    );

    String urlFull = apiUrl + apiKey;

    try {
      // Trimitem cererea și primim raspunsul direct ca Map (dicționar)
      Map<String, Object> response = restTemplate.postForObject(urlFull, requestBody, Map.class);

      // Navigam prin structura Map-ului generat
      List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");

      if (candidates == null || candidates.isEmpty()) {

        logger.warn("Gemini returned no candidates for property id {}", propertyId);

        return "No candidates were found in the Gemini response.";
      }

      Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
      List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

      logger.info("AI review summary generated successfully for property id {}", propertyId);

      return (String) parts.get(0).get("text");

    } catch (RestClientResponseException e) {

      logger.error("Gemini API error for property id {}. Status: {}, Body: {}", propertyId, e.getStatusCode(), e.getResponseBodyAsString());

      return "AI summary is temporarily unavailable. Please try again later.";

    } catch (Exception e) {

      logger.error("Unexpected error while processing AI summary for property id {}", propertyId, e);

      return "Error while processing the AI summary. Please try again later.";
    }
  }

}
