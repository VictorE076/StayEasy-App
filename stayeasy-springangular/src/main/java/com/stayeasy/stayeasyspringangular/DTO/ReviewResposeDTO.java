package com.stayeasy.stayeasyspringangular.DTO;

import java.time.LocalDateTime;

public class ReviewResposeDTO {
  public record ReviewResponse(
    Integer rating,
    String comment,
    String userName,
    LocalDateTime createdAt
  ) {}

}
