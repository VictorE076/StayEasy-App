package com.stayeasy.stayeasyspringangular.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRequestDTO {

  @NotNull
  @Min(1) @Max(5)
  private Integer rating; // 1-5 Stars rating

  @Size(max = 2000)
  private String comment; // String comment of maximum 200 characters

}

