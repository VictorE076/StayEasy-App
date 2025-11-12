package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.User;
import com.stayeasy.stayeasyspringangular.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public DatabaseUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

//  @Override
//  public UserDetails loadUserByUsername(String username)
//    throws UsernameNotFoundException {
//    // Cautam userul in DB dupa username
//    User user = userRepository
//      .findByUsername(username)
//      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//    // Daca NU implementeaza UserDetails, il mapam la un UserDetails Spring:
//    return org.springframework.security.core.userdetails.User
//      .withUsername(user.getUsername())
//      .password(user.getPasswordHash())
//      .build();
//  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    var user =
      userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    System.out.println(">>> loadUserByUsername: " + user.getUsername() + " | pass=" + user.getPasswordHash());

    return org.springframework.security.core.userdetails.User
      .withUsername(user.getUsername())
      .password(user.getPasswordHash())                // hash-ul din password_hash
      .roles(user.getRole().name())
      .build();
  }
}

