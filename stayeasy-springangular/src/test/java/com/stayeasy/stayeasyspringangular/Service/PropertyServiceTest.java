package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.*;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.*;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.stayeasy.stayeasyspringangular.exception.ForbiddenActionException;
import com.stayeasy.stayeasyspringangular.exception.ResourceNotFoundException;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import org.springframework.data.domain.PageImpl;

import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.stayeasy.stayeasyspringangular.Repository.AmenityRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private PropertyService propertyService;

  @Mock
  private AmenityRepository amenityRepository;

  // helper pt authentication
  private void mockAuth(String username) {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(username);
    // Se evita UnnecessaryStubbingException
    lenient().when(auth.getAuthorities()).thenReturn(List.of()); // default: nu e admin

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private void mockAdminAuth(String username) {
    Authentication auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(username);
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
      .when(auth).getAuthorities();

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private PropertyRequestDTO validPropertyDto() {
    PropertyRequestDTO dto = new PropertyRequestDTO();

    dto.setTitle("Updated property");
    dto.setDescription("Updated description");
    dto.setCity("Bucharest");
    dto.setAddress("Updated address");
    dto.setPricePerNight(BigDecimal.valueOf(150));
    dto.setMaxGuests(4);
    dto.setPropertyType(PropertyType.APARTMENT);

    return dto;
  }

  private User createUser(Integer id , String username, Role role) {
    User user = new User();

//    user.setId(id);
    ReflectionTestUtils.setField(user, "id", id);

    user.setUsername(username);
    user.setRole(role);
    return user;
  }

  private Property createProperty(User owner) {
    Property property = new Property();

//    property.setId(1L);
    ReflectionTestUtils.setField(property, "id", 1L);

    property.setTitle("Test");
    property.setCity("Bucharest");
    property.setPricePerNight(BigDecimal.valueOf(100));
    property.setOwner(owner);
    return property;
  }

  @BeforeEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }


  // GET ALL

  @Test
  void getAllProperties_shouldReturnList() {
    Property property = new Property();

//    property.setId(1L);
    ReflectionTestUtils.setField(property, "id", 1L);

    when(propertyRepository.findAll()).thenReturn(List.of(property));

    var result = propertyService.getAllProperties();

    assertEquals(1, result.size());
  }


  // GET BY ID

  @Test
  void getPropertyById_shouldReturnProperty() {
    Property property = new Property();

//    property.setId(1L);
    ReflectionTestUtils.setField(property, "id", 1L);

    when(propertyRepository.findById(1L))
      .thenReturn(Optional.of(property));

    var result = propertyService.getPropertyById(1L);

    assertNotNull(result);
  }

  @Test
  void getPropertyById_shouldThrowIfNotFound() {
    when(propertyRepository.findById(1L))
      .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
      () -> propertyService.getPropertyById(1L));
  }


  // SEARCH

  @Test
  void search_byCityAndPrice() {
    when(propertyRepository.findByCityAndPricePerNightLessThanEqual(any(), any()))
      .thenReturn(List.of(new Property()));

    var result = propertyService.search("Bucharest", BigDecimal.valueOf(100));

    assertEquals(1, result.size());
  }

  @Test
  void search_byCityOnly() {
    when(propertyRepository.findByCity(any()))
      .thenReturn(List.of(new Property()));

    var result = propertyService.search("Bucharest", null);

    assertEquals(1, result.size());
  }

  @Test
  void search_byPriceOnly() {
    when(propertyRepository.findByPricePerNightLessThanEqual(any()))
      .thenReturn(List.of(new Property()));

    var result = propertyService.search(null, BigDecimal.valueOf(100));

    assertEquals(1, result.size());
  }

  @Test
  void search_noFilters() {
    when(propertyRepository.findAll())
      .thenReturn(List.of(new Property()));

    var result = propertyService.search(null, null);

    assertEquals(1, result.size());
  }


  // CREATE PROPERTY

  @Test
  void createProperty_shouldCreateAndPromoteUserToHost() {
    mockAuth("user1");

    User user = createUser(1, "user1", Role.GUEST);

    when(userRepository.findByUsername("user1"))
      .thenReturn(Optional.of(user));

    when(propertyRepository.save(any()))
      .thenAnswer(i -> i.getArgument(0));

    PropertyRequestDTO dto = new PropertyRequestDTO();
    dto.setTitle("Test");
    dto.setCity("Bucharest");
    dto.setPricePerNight(BigDecimal.valueOf(100));

    var result = propertyService.createProperty(dto);

    assertNotNull(result);
    assertEquals(Role.HOST, user.getRole());
    verify(userRepository).save(user);
  }


  // DELETE PROPERTY

  @Test
  void deleteProperty_shouldWorkForOwner() {
    mockAuth("user1");

    User user = createUser(1, "user1", Role.HOST);
    Property property = createProperty(user);

    when(userRepository.findByUsername("user1"))
      .thenReturn(Optional.of(user));

    when(propertyRepository.findById(1L))
      .thenReturn(Optional.of(property));

    when(propertyRepository.existsByOwner_Id(1))
      .thenReturn(false);

    propertyService.deleteProperty(1L);

    verify(propertyRepository).delete(property);
  }

  @Test
  void deleteProperty_shouldFailIfNotOwner() {
    mockAuth("user1");

    User currentUser = createUser(1, "user1", Role.HOST);
    User owner = createUser(2, "other", Role.HOST);

    Property property = createProperty(owner);

    when(userRepository.findByUsername("user1"))
      .thenReturn(Optional.of(currentUser));

    when(propertyRepository.findById(1L))
      .thenReturn(Optional.of(property));

    assertThrows(ForbiddenActionException.class,
      () -> propertyService.deleteProperty(1L));
  }


  // DETAIL

  @Test
  void getPropertyDetail_shouldReturnDetails() {
    Property property = new Property();

//    property.setId(1L);
    ReflectionTestUtils.setField(property, "id", 1L);

    when(propertyRepository.findById(1L))
      .thenReturn(Optional.of(property));

    var result = propertyService.getPropertyDetailById(1L);

    assertNotNull(result);
  }


  // PAGINATION

  @Test
  void getPropertiesPage_validRequest_shouldReturnPageResponse() {
    Property property = new Property();
    ReflectionTestUtils.setField(property, "id", 1L);
    property.setTitle("Test property");

    when(propertyRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(property)));

    PageResponseDTO<PropertyResponseDTO> result =
      propertyService.getPropertiesPage(0, 5, "createdAt", "desc");

    assertEquals(1, result.getContent().size());
    assertEquals(0, result.getPageNumber());
    assertEquals("createdAt", result.getSortBy());
    assertEquals("desc", result.getDirection());
  }

  @Test
  void getPropertiesPage_negativePage_shouldThrowBadRequest() {
    assertThrows(BadRequestException.class,
      () -> propertyService.getPropertiesPage(-1, 5, "createdAt", "desc"));

    verify(propertyRepository, never()).findAll(any(org.springframework.data.domain.Pageable.class));
  }

  @Test
  void getPropertiesPage_invalidSize_shouldThrowBadRequest() {
    assertThrows(BadRequestException.class,
      () -> propertyService.getPropertiesPage(0, 0, "createdAt", "desc"));

    verify(propertyRepository, never()).findAll(any(org.springframework.data.domain.Pageable.class));
  }


  // UPDATE PROPERTY

  @Test
  void updateProperty_ownerCanUpdateImagesAmenitiesAndHouseRules() {
    mockAuth("owner");

    User owner = createUser(1, "owner", Role.HOST);
    Property property = createProperty(owner);

    PropertyRequestDTO dto = validPropertyDto();
    dto.setImagePaths(List.of("img1.jpg", "img2.jpg"));
    dto.setAmenityNames(List.of("WiFi", "WiFi", " Pool "));

    dto.setHouseRules(HouseRulesDTO.builder()
      .smokingAllowed(true)
      .petsAllowed(true)
      .checkInTime("15:00")
      .checkOutTime("11:00")
      .build());

    Amenity wifi = Amenity.builder().name("WiFi").build();

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
    when(amenityRepository.findByNameIgnoreCase("WiFi")).thenReturn(Optional.of(wifi));
    when(amenityRepository.findByNameIgnoreCase("Pool")).thenReturn(Optional.empty());
    when(amenityRepository.save(any(Amenity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = propertyService.updateProperty(1L, dto);

    assertNotNull(result);
    assertEquals("Updated property", property.getTitle());
    assertEquals("Bucharest", property.getCity());
    assertEquals(2, property.getImages().size());
    assertEquals(2, property.getAmenities().size());

    assertNotNull(property.getHouseRules());
    assertTrue(property.getHouseRules().isSmokingAllowed());
    assertTrue(property.getHouseRules().isPetsAllowed());
    assertEquals("15:00", property.getHouseRules().getCheckInTime().toString());
    assertEquals("11:00", property.getHouseRules().getCheckOutTime().toString());

    verify(propertyRepository).save(property);
  }

  @Test
  void updateProperty_adminCanUpdatePropertyOwnedByAnotherUser() {
    mockAdminAuth("admin");

    User admin = createUser(99, "admin", Role.ADMIN);
    User owner = createUser(1, "owner", Role.HOST);
    Property property = createProperty(owner);

    PropertyRequestDTO dto = validPropertyDto();
    dto.setTitle("Updated by admin");

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
    when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = propertyService.updateProperty(1L, dto);

    assertNotNull(result);
    assertEquals("Updated by admin", property.getTitle());
    verify(propertyRepository).save(property);
  }

  @Test
  void createProperty_invalidCheckInTime_shouldThrowBadRequest() {
    mockAuth("owner");

    User owner = createUser(1, "owner", Role.HOST);

    PropertyRequestDTO dto = validPropertyDto();
    dto.setHouseRules(HouseRulesDTO.builder()
      .smokingAllowed(false)
      .petsAllowed(true)
      .checkInTime("25:99")
      .checkOutTime("11:00")
      .build());

    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));

    assertThrows(BadRequestException.class, () -> propertyService.createProperty(dto));

    verify(propertyRepository, never()).save(any(Property.class));
  }

  @Test
  void getPropertyDetail_shouldMapNestedImagesAmenitiesReviewsHouseRulesAndAvailability() {
    User owner = createUser(1, "owner", Role.HOST);
    Property property = createProperty(owner);

    property.setDescription("Nice property");
    property.setAddress("Test address");
    property.setMaxGuests(3);
    property.setPropertyType(PropertyType.APARTMENT);

    PropertyImage image = PropertyImage.builder()
      .imagePath("img1.jpg")
      .property(property)
      .build();

    Amenity amenity = Amenity.builder()
      .name("WiFi")
      .build();

    Review review1 = Review.builder()
      .rating(5)
      .comment("Great")
      .user(owner)
      .property(property)
      .build();

    Review review2 = Review.builder()
      .rating(3)
      .comment("Ok")
      .user(owner)
      .property(property)
      .build();

    HouseRules rules = HouseRules.builder()
      .smokingAllowed(false)
      .petsAllowed(true)
      .checkInTime(LocalTime.parse("14:00"))
      .checkOutTime(LocalTime.parse("12:00"))
      .property(property)
      .build();

    Availability availability = Availability.builder()
      .availableFrom(LocalDate.of(2026, 7, 1))
      .availableTo(LocalDate.of(2026, 7, 10))
      .property(property)
      .build();

    property.setImages(List.of(image));
    property.setAmenities(List.of(amenity));
    property.setReviews(List.of(review1, review2));
    property.setHouseRules(rules);
    property.setAvailability(List.of(availability));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    var result = propertyService.getPropertyDetailById(1L);

    assertNotNull(result);
    assertEquals(1, result.getImages().size());
    assertEquals(1, result.getAmenities().size());
    assertEquals(2, result.getReviews().size());
    assertEquals(1, result.getAvailability().size());
    assertEquals(2, result.getTotalReviews());
    assertEquals(4.0, result.getAverageRating(), 0.001);
    assertNotNull(result.getHouseRules());
    assertEquals("14:00", result.getHouseRules().getCheckInTime());
  }

}
