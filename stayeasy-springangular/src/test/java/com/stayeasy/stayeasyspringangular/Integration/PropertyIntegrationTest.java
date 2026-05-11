package com.stayeasy.stayeasyspringangular.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.PropertyType;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.PropertyRepository;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PropertyIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    propertyRepository.deleteAll();
    userRepository.deleteAll();
  }

  //Scenariu 1: CREATE PROPERTY

  @Test
  @WithMockUser(username = "user1", roles = {"GUEST"})
  void createProperty_integrationTest() throws Exception {

    User user = new User();
    user.setUsername("user1");
    user.setRole(Role.GUEST);
    user.setEmail("test_mock@mock.com");
    user.setPasswordHash("123454321");

    userRepository.save(user);

    String json = """
            {
              "title": "Test Property",
              "description": "A nice place to stay",
              "city": "Bucharest",
              "address": "St Test 123",
              "pricePerNight": 100,
              "maxGuests": 2,
              "propertyType": "STUDIO"
            }
            """;

    mockMvc.perform(post("/api/properties")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.title").value("Test Property"));
  }

  //Scenariu 2: GET PROPERTY

  @Test
  @WithMockUser(username = "user1", roles = {"HOST"})
  void getPropertyById_integrationTest() throws Exception {

    User user = new User();
    user.setUsername("user1");
    user.setRole(Role.HOST);
    user.setEmail("host@mock.com");
    user.setPasswordHash("123456");

    user = userRepository.save(user);

    Property property = new Property();
    property.setTitle("TestP");
    property.setDescription("Test Property");
    property.setCity("Bucharest");
    property.setPricePerNight(BigDecimal.valueOf(100));
    property.setMaxGuests(2);
    property.setPropertyType(PropertyType.STUDIO);
    property.setOwner(user);

    property = propertyRepository.save(property);

    mockMvc.perform(get("/api/properties/" + property.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("TestP"));
  }

  //Scenariu 3: DELETE (FORBIDDEN)

  @Test
  @WithMockUser(username = "user1", roles = {"HOST"})
  void deleteProperty_integrationTest() throws Exception {

    // user autentificat
    User currentUser = new User();
    currentUser.setUsername("user1");
    currentUser.setRole(Role.HOST);
    currentUser.setEmail("host@test.com");
    currentUser.setPasswordHash("123456");

    userRepository.save(currentUser);

    // owner-ul proprietății
    User owner = new User();
    owner.setUsername("otherUser");
    owner.setRole(Role.HOST);
    owner.setEmail("other@mock.com");
    owner.setPasswordHash("123456");

    owner = userRepository.save(owner);

    Property property = new Property();
    property.setTitle("Test2");
    property.setDescription("Test Property delete");
    property.setCity("Bucharest");
    property.setPricePerNight(BigDecimal.valueOf(100));
    property.setMaxGuests(2);
    property.setPropertyType(PropertyType.STUDIO);
    property.setOwner(owner);

    property = propertyRepository.save(property);

    mockMvc.perform(delete("/api/properties/" + property.getId()))
      .andExpect(status().isForbidden());
  }
}
