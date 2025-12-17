package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.PropertyRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.PropertyResponseDTO;
import com.stayeasy.stayeasyspringangular.Service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

  private final PropertyService propertyService;

  // GET /properties
  @GetMapping
  public List<PropertyResponseDTO> getAll() {
    return propertyService.getAllProperties();
  }

  // GET /properties/{id}
  @GetMapping("/{id}")
  public PropertyResponseDTO getById(@PathVariable Long id) {
    return propertyService.getPropertyById(id);
  }

  // GET /properties/search
  @GetMapping("/search")
  public List<PropertyResponseDTO> search(
    @RequestParam(required = false) String city,
    @RequestParam(required = false) BigDecimal maxPrice
  ) {
    return propertyService.search(city, maxPrice);
  }

  // POST /properties
  @PostMapping
  public PropertyResponseDTO create(@RequestBody PropertyRequestDTO dto) {
    return propertyService.createProperty(dto);
  }

  // DELETE /properties/{id}
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    propertyService.deleteProperty(id);
  }

}
