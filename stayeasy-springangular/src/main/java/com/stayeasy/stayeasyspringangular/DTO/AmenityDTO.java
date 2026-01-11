package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmenityDTO {
  private Long id;
  private String name;
}
