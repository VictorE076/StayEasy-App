package com.stayeasy.stayeasyspringangular.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseRulesDTO {
  private Long id;

  private boolean smokingAllowed;
  private boolean petsAllowed;

  @Pattern(
    regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
    message = "Check-in time must use HH:mm format"
  )
  private String checkInTime;

  @Pattern(
    regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
    message = "Check-out time must use HH:mm format"
  )
  private String checkOutTime;
}
