package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AvailabilityDTO {
  private Long id;
  private LocalDate availableFrom;
  private LocalDate availableTo;
}
