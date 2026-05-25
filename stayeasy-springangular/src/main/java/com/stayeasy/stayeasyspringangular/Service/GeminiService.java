package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository; // presupun că ai așa ceva
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiService {

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
    var property = propertyRepository.findById(propertyId)
      .orElseThrow(() -> new RuntimeException("Property not found"));

    List<Review> reviews = property.getReviews();

    if (reviews == null || reviews.isEmpty()) {
      return "Această proprietate nu are încă recenzii pentru a fi rezumate.";
    }

    String allReviewsText = reviews.stream()
      .map(r -> "- " + r.getComment() + " (Rating: " + r.getRating() + "/5)")
      .collect(Collectors.joining("\n"));

    String prompt = "Ești un asistent inteligent pentru o platformă de rezervări cazări (StayEasy). " +
      "Mai jos ai o listă de recenzii lăsate de utilizatori pentru o proprietate. " +
      "Te rog să faci un rezumat scurt, obiectiv și structurat în 3 idei principale (cu bullet-points), în limba română.\n\n" +
      "Recenzii:\n" + allReviewsText;

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
        return "Nu s-au găsit candidați în răspunsul Gemini.";
      }

      Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
      List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

      return (String) parts.get(0).get("text");

    } catch (org.springframework.web.client.HttpClientErrorException e) {

      System.err.println("[GEMINI ERROR STATUS]: " + e.getStatusCode());
      System.err.println("[GEMINI ERROR BODY]: " + e.getResponseBodyAsString());
      return "Eroare API Gemini: " + e.getStatusCode() + ". Verifică consola backend-ului!";

    } catch (Exception e) {
      System.err.println("[GENERAL ERROR]: " + e.getMessage());
      return "Eroare la procesarea rezumatului AI: " + e.getMessage();
    }
  }

}
