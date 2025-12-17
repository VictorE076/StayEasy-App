package com.stayeasy.stayeasyspringangular.DTO;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyRequestDTO {

  private String title;
  private String description;
  private String city;
  private String address;
  private BigDecimal pricePerNight;
  private Integer maxGuests;
  private PropertyType propertyType;

  private Integer ownerId;

  private List<String> imagePaths;

}
