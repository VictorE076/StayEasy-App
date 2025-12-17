package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PropertyResponseDTO {

  private Long id;
  private String title;
  private String description;
  private String city;
  private String address;
  private BigDecimal pricePerNight;
  private Integer maxGuests;
  private String propertyType;

  private String ownerUsername;

  private List<String> images;

}
