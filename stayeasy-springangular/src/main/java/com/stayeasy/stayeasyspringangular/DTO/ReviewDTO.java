package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDTO {
  private Long id;
  private Integer rating;
  private String comment;
  private String userName;
  private LocalDateTime createdAt;
}
