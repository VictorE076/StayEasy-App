package com.stayeasy.stayeasyspringangular.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponseDTO<T> {

  private List<T> content;

  private int pageNumber;
  private int pageSize;

  private long totalElements;
  private int totalPages;

  private boolean first;
  private boolean last;

  private String sortBy;
  private String direction;
}
