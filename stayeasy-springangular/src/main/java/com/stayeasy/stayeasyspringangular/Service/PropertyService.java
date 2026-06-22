package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.*;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.*;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.stayeasy.stayeasyspringangular.exception.ForbiddenActionException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import com.stayeasy.stayeasyspringangular.exception.UnauthorizedActionException;

import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class PropertyService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

  private final PropertyRepository propertyRepository;
  private final UserRepository userRepository;

  public List<PropertyResponseDTO> getAllProperties() {
    return propertyRepository.findAll()
      .stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  public PageResponseDTO<PropertyResponseDTO> getPropertiesPage(
    int page, int size, String sortBy, String direction
  ) {
    if (page < 0) {
      throw new BadRequestException("Page number cannot be negative");
    }

    if (size < 1 || size > 50) {
      throw new BadRequestException("Page size must be between 1 and 50");
    }

    List<String> allowedSortFields = List.of(
      "id",
      "title",
      "city",
      "pricePerNight",
      "maxGuests",
      "createdAt"
    );

    if (!allowedSortFields.contains(sortBy)) {
      throw new BadRequestException("Invalid sort field: " + sortBy);
    }

    Sort sort;

    if ("desc".equalsIgnoreCase(direction)) {
      sort = Sort.by(sortBy).descending();
    } else if ("asc".equalsIgnoreCase(direction)) {
      sort = Sort.by(sortBy).ascending();
    } else {
      throw new BadRequestException("Sort direction must be asc or desc");
    }

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Property> propertyPage = propertyRepository.findAll(pageable);

    List<PropertyResponseDTO> content = propertyPage.getContent()
      .stream()
      .map(this::mapToResponse)
      .toList();

    return PageResponseDTO.<PropertyResponseDTO>builder()
      .content(content)
      .pageNumber(propertyPage.getNumber())
      .pageSize(propertyPage.getSize())
      .totalElements(propertyPage.getTotalElements())
      .totalPages(propertyPage.getTotalPages())
      .first(propertyPage.isFirst())
      .last(propertyPage.isLast())
      .sortBy(sortBy)
      .direction(direction.toLowerCase())
      .build();
  }

  public PropertyResponseDTO getPropertyById(Long id) {
    Property property = propertyRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
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

  public PropertyResponseDTO createProperty(PropertyRequestDTO dto) { // The created property always belongs to the logged-in account

    User currentUser = getCurrentUser();

    logger.info("Creating property for user id {}", currentUser.getId());

    // Once a GUEST user adds its first property, it becomes a HOST.
    if (currentUser.getRole() == Role.GUEST) {
      currentUser.setRole(Role.HOST);
      userRepository.save(currentUser);

      logger.info("User id {} was promoted from GUEST to HOST after creating a property", currentUser.getId());

    }


    Property property = Property.builder()
      .title(dto.getTitle())
      .description(dto.getDescription())
      .city(dto.getCity())
      .address(dto.getAddress())
      .pricePerNight(dto.getPricePerNight())
      .maxGuests(dto.getMaxGuests())
      .propertyType(dto.getPropertyType())
      .owner(currentUser)
      .build();

    var imagePaths = dto.getImagePaths() == null ? List.<String>of() : dto.getImagePaths();
    List<PropertyImage> images = imagePaths.stream()
      .map(path -> PropertyImage.builder()
        .imagePath(path)
        .property(property)
        .build())
      .toList();

    property.setImages(images);

    Property savedProperty = propertyRepository.save(property);

    logger.info("Property created successfully with id {} by user id {}", savedProperty.getId(), currentUser.getId());

    return mapToResponse(savedProperty);

  }

  @Transactional
  public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO dto) { // Ownership checking (except for "ROLE_ADMIN")

    Property property = propertyRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    User currentUser = getCurrentUser();

    Integer ownerId = property.getOwner() == null ? null : property.getOwner().getId();
    Integer currentUserId = currentUser.getId();

    boolean isAdmin = getAuthenticationContext().getAuthorities().stream()
      .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

    if (!isAdmin) { // Only normal (GUEST or HOST) user must also be the property's owner.
      if (ownerId == null || !ownerId.equals(currentUserId)) {

        logger.warn("Property update denied. Property id {}, owner id {}, current user id {}", id, ownerId, currentUserId);

        throw new ForbiddenActionException("You are not allowed to update this property");
      }
    }

    property.setTitle(dto.getTitle());
    property.setDescription(dto.getDescription());
    property.setCity(dto.getCity());
    property.setAddress(dto.getAddress());
    property.setPricePerNight(dto.getPricePerNight());
    property.setMaxGuests(dto.getMaxGuests());
    property.setPropertyType(dto.getPropertyType());

    var imagePaths = dto.getImagePaths() == null ? List.<String>of() : dto.getImagePaths();

    List<PropertyImage> updatedImages = imagePaths.stream()
      .map(path -> PropertyImage.builder()
        .imagePath(path)
        .property(property)
        .build())
      .toList();

    if (property.getImages() == null) {
      property.setImages(new java.util.ArrayList<>());
    }

    property.getImages().clear();
    property.getImages().addAll(updatedImages);

    Property savedProperty = propertyRepository.save(property);

    logger.info("Property id {} updated successfully by user id {}", id, currentUserId);

    return mapToResponse(savedProperty);
  }

  public void deleteProperty(Long id) { // Ownership checking (except for "ROLE_ADMIN")

    Property property = propertyRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    User currentUser = getCurrentUser();

    Integer ownerId = (property.getOwner() == null) ? null : property.getOwner().getId();
    Integer currentUserId = currentUser.getId();

    boolean isAdmin = getAuthenticationContext().getAuthorities().stream()
      .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

    if (!isAdmin) { // Only normal (GUEST or HOST) user must also be the property's owner.
      if (ownerId == null || !ownerId.equals(currentUserId)) {

        logger.warn("Property delete denied. Property id {}, owner id {}, current user id {}", id, ownerId, currentUserId);

        throw new ForbiddenActionException("You are not allowed to delete this property");
      }
    }

    User owner = property.getOwner(); // Keep the owner before deleting its property.

    // DELETE Property
    propertyRepository.delete(property);

    logger.info("Property id {} deleted successfully by user id {}", id, currentUserId);

    // After property deletion, if the owner is not ADMIN,
    // then check if it still has at least one property registered.
    if (owner != null && owner.getRole() != Role.ADMIN) {
      boolean stillHasProperties = propertyRepository.existsByOwner_Id(owner.getId());
      Role newRole = (stillHasProperties) ? Role.HOST : Role.GUEST;

      if (owner.getRole() != newRole) {
        owner.setRole(newRole);
        userRepository.save(owner);

        logger.info("Owner user id {} role changed to {} after property deletion", owner.getId(), newRole);

      }

    }

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
      .createdAt(property.getCreatedAt())
      .images(images)
      .build();
  }


  // Auth helpers
  private Authentication getAuthenticationContext() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private User getCurrentUser() {
    Authentication auth = getAuthenticationContext();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedActionException("You must be logged in");
    }

    String username = auth.getName();
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UnauthorizedActionException("Authenticated user was not found"));
  }

  public PropertyDetailDTO getPropertyDetailById(Long id) {
    Property property = propertyRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

    return mapToDetailResponse(property);
  }

  private PropertyDetailDTO mapToDetailResponse(Property property) {

    List<String> images = property.getImages() == null ? List.of()
      : property.getImages().stream()
      .map(PropertyImage::getImagePath)
      .toList();

    List<AmenityDTO> amenities = property.getAmenities() == null ? List.of()
      : property.getAmenities().stream()
      .map(amenity -> AmenityDTO.builder()
        .id(amenity.getId())
        .name(amenity.getName())
        .build())
      .toList();

    List<ReviewDTO> reviews = property.getReviews() == null ? List.of()
      : property.getReviews().stream()
      .map(review -> ReviewDTO.builder()
        .id(review.getId())
        .rating(review.getRating())
        .comment(review.getComment())
        .userName(review.getUser() != null ? review.getUser().getUsername() : "Unknown")
        .createdAt(review.getCreatedAt())
        .build())
      .toList();

    Double averageRating = reviews.isEmpty() ? null
      : reviews.stream()
      .mapToInt(ReviewDTO::getRating)
      .average()
      .orElse(0.0);

    HouseRulesDTO houseRulesDTO = null;
    if (property.getHouseRules() != null) {
      HouseRules rules = property.getHouseRules();
      houseRulesDTO = HouseRulesDTO.builder()
        .id(rules.getId())
        .smokingAllowed(rules.isSmokingAllowed())
        .petsAllowed(rules.isPetsAllowed())
        .checkInTime(rules.getCheckInTime() != null ? rules.getCheckInTime().toString() : null)
        .checkOutTime(rules.getCheckOutTime() != null ? rules.getCheckOutTime().toString() : null)
        .build();
    }

    List<AvailabilityDTO> availabilityList = property.getAvailability() == null ? List.of()
      : property.getAvailability().stream()
      .map(avail -> AvailabilityDTO.builder()
        .id(avail.getId())
        .availableFrom(avail.getAvailableFrom())
        .availableTo(avail.getAvailableTo())
        .build())
      .toList();

    return PropertyDetailDTO.builder()
      .id(property.getId())
      .title(property.getTitle())
      .description(property.getDescription())
      .city(property.getCity())
      .address(property.getAddress())
      .pricePerNight(property.getPricePerNight())
      .maxGuests(property.getMaxGuests())
      .propertyType(property.getPropertyType() != null ? property.getPropertyType().name() : null)
      .ownerUsername(property.getOwner() != null ? property.getOwner().getUsername() : null)
      .createdAt(property.getCreatedAt())
      .images(images)
      .amenities(amenities)
      .reviews(reviews)
      .houseRules(houseRulesDTO)
      .availability(availabilityList)
      .averageRating(averageRating)
      .totalReviews(reviews.size())
      .build();
  }

}
