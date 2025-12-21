package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.PropertyRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.PropertyResponseDTO;
import com.stayeasy.stayeasyspringangular.Service.PropertyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Validated
public class PropertyController {

  private final PropertyService propertyService;

  // GET /api/properties
  @GetMapping
  public List<PropertyResponseDTO> getAll() {
    return propertyService.getAllProperties();
  }

  // GET /api/properties/{id}
  @GetMapping("/{id}")
  public PropertyResponseDTO getById(@PathVariable Long id) {
    return propertyService.getPropertyById(id);
  }

  // GET /api/properties/search
  @GetMapping("/search")
  public List<PropertyResponseDTO> search(
    @RequestParam(required = false) @Size(max = 80) String city,
    @RequestParam(required = false) @DecimalMin(value = "0.00", inclusive = false) BigDecimal maxPrice
  ) {
    return propertyService.search(city, maxPrice);
  }

  // POST /api/properties
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PropertyResponseDTO create(@Valid @RequestBody PropertyRequestDTO dto) {
    return propertyService.createProperty(dto);
  }

  // DELETE /api/properties/{id}
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    propertyService.deleteProperty(id);
  }

}
