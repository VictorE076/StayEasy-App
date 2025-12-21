package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.PropertyRequestDTO;
import com.stayeasy.stayeasyspringangular.DTO.PropertyResponseDTO;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.PropertyImage;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

  private final PropertyRepository propertyRepository;
  private final UserRepository userRepository;

  public List<PropertyResponseDTO> getAllProperties() {
    return propertyRepository.findAll()
      .stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  public PropertyResponseDTO getPropertyById(Long id) {
    Property property = propertyRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    return mapToResponse(property);
  }

  public List<PropertyResponseDTO> search(String city, BigDecimal maxPrice) {

    List<Property> properties;

    if (city != null && maxPrice != null) {
      properties = propertyRepository
        .findByCityAndPricePerNightLessThanEqual(city, maxPrice);
    } else if (city != null) {
      properties = propertyRepository.findByCity(city);
    } else if (maxPrice != null) {
      properties = propertyRepository.findByPricePerNightLessThanEqual(maxPrice);
    } else {
      properties = propertyRepository.findAll();
    }

    return properties.stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  public PropertyResponseDTO createProperty(PropertyRequestDTO dto) {

    User owner = userRepository.findById(dto.getOwnerId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

    Property property = Property.builder()
      .title(dto.getTitle())
      .description(dto.getDescription())
      .city(dto.getCity())
      .address(dto.getAddress())
      .pricePerNight(dto.getPricePerNight())
      .maxGuests(dto.getMaxGuests())
      .propertyType(dto.getPropertyType())
      .owner(owner)
      .build();


    var imagePaths = dto.getImagePaths() == null ? List.<String>of() : dto.getImagePaths();
    List<PropertyImage> images = imagePaths
      .stream()
      .map(path -> PropertyImage.builder()
        .imagePath(path)
        .property(property)
        .build())
      .collect(Collectors.toList());

    property.setImages(images);

    return mapToResponse(propertyRepository.save(property));
  }

  public void deleteProperty(Long id) {
    if (!propertyRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
    }
    propertyRepository.deleteById(id);
  }

  private PropertyResponseDTO mapToResponse(Property property) {

    var images = property.getImages() == null ? List.<String>of()
      : property.getImages().stream()
      .map(PropertyImage::getImagePath)
      .toList();

    String propertyType = property.getPropertyType() == null ? null : property.getPropertyType().name();
    String ownerUsername = property.getOwner() == null ? null : property.getOwner().getUsername();

    return PropertyResponseDTO.builder()
      .id(property.getId())
      .title(property.getTitle())
      .description(property.getDescription())
      .city(property.getCity())
      .address(property.getAddress())
      .pricePerNight(property.getPricePerNight())
      .maxGuests(property.getMaxGuests())
      .propertyType(propertyType)
      .ownerUsername(ownerUsername)
      .images(images)
      .build();
  }

}
