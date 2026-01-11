package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Data;

public class ReviewRequestDTO {
  @Data
  public class ReviewRequest {
    private Integer rating;
    private String comment;
    private Long userId;
    private Long userName;
    private Long propertyId;
  }


}
