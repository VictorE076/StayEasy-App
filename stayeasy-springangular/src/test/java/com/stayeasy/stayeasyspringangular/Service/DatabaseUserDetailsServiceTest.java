package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Role;
import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DatabaseUserDetailsService databaseUserDetailsService;

  private User createUser() {

    User user = new User();

    user.setUsername("test");
    user.setPasswordHash("123456789");
    user.setRole(Role.HOST);

    return user;
  }

  //load user by username

  @Test
  void loadUserByUsername_shouldReturnUserDetails() {

    User user = createUser();

    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

    UserDetails result = databaseUserDetailsService.loadUserByUsername("test");

    assertNotNull(result);

    assertEquals("test", result.getUsername());
    assertEquals("123456789", result.getPassword());
    assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HOST")));

    verify(userRepository).findByUsername("test");
  }

  @Test
  void loadUserByUsername_shouldThrowIfUserNotFound() {

    when(userRepository.findByUsername("doesn't_exist")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> databaseUserDetailsService.loadUserByUsername("doesn't_exist"));

    verify(userRepository).findByUsername("doesn't_exist");
  }
}
