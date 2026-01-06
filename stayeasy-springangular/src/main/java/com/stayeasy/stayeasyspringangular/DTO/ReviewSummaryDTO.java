package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewSummaryDTO {

  private Long propertyId;
  private Double averageRating;
  private Long reviewsCount;

}
