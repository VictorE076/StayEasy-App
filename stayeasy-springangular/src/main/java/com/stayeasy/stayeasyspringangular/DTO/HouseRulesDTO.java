package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseRulesDTO {
  private Long id;
  private boolean smokingAllowed;
  private boolean petsAllowed;
  private String checkInTime;
  private String checkOutTime;
}
