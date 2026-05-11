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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private PropertyService propertyService;

  // helper pentru authentication
  private void mockAuth(String username) {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(username);
    when(auth.getAuthorities()).thenReturn(List.of()); // default: nu e admin

    SecurityContextHolder.getContext().setAuthentication(auth);
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

    assertThrows(ResponseStatusException.class,
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

    assertThrows(ResponseStatusException.class,
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
}
