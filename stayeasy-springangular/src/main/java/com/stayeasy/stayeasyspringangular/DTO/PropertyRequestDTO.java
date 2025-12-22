package com.stayeasy.stayeasyspringangular.DTO;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.PropertyType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyRequestDTO {

  @NotBlank(message = "Title is required")
  @Size(max = 120, message = "Title must be at most 120 characters")
  private String title;

  @NotBlank(message = "City is required")
  @Size(max = 80, message = "City must be at most 80 characters")
  private String city;

  @NotBlank(message = "Address is required")
  @Size(max = 200, message = "Address must be at most 200 characters")
  private String address;

  @Size(max = 2000, message = "Description must be at most 2000 characters")
  private String description; // optional

  @NotNull(message = "Price per night is required")
  @DecimalMin(value = "0.00", inclusive = false, message = "Price must be > 0")
  @Digits(integer = 10, fraction = 2, message = "Price must have up to 2 decimals")
  private BigDecimal pricePerNight;

  @NotNull(message = "Max guests is required")
  @Min(value = 1, message = "Max guests must be >= 1")
  @Max(value = 50, message = "Max guests must be <= 50")
  private Integer maxGuests;

  @NotNull(message = "Property type is required")
  private PropertyType propertyType; // enum

  @Size(max = 20, message = "At most 20 images")
  private List<
    @NotBlank(message = "Image path must not be blank")
    @Size(max = 500, message = "Image path too long")
      String
    > imagePaths = new ArrayList<>();

}
