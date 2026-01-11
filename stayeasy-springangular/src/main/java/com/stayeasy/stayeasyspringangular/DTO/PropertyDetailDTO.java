package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PropertyDetailDTO {
  private Long id;
  private String title;
  private String description;
  private String city;
  private String address;
  private BigDecimal pricePerNight;
  private Integer maxGuests;
  private String propertyType;
  private String ownerUsername;
  private Long ownerId;
  private List<String> images;
  private List<AmenityDTO> amenities;
  private List<ReviewDTO> reviews;
  private HouseRulesDTO houseRules;
  private List<AvailabilityDTO> availability;
  private Double averageRating;
  private Integer totalReviews;
  private LocalDateTime createdAt;
}

