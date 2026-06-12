package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(DatabaseUserDetailsService.class);

  private final UserRepository userRepository;

  public DatabaseUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {

    logger.debug("Loading user details for username {}", username);

    var user = userRepository.findByUsername(username)
      .orElseThrow(() -> {

        logger.warn("User not found during authentication: {}", username);

        return new UsernameNotFoundException("User not found");
      });

    return org.springframework.security.core.userdetails.User
      .withUsername(user.getUsername())
      .password(user.getPasswordHash()) // hash-ul din password_hash
      .roles(user.getRole().name())
      .build();
  }

}

