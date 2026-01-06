package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {

  private Long id;
  private Long propertyId;
  private Integer rating;
  private String comment;
  private String username;
  private LocalDateTime createdAt;

}


