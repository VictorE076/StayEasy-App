package com.stayeasy.stayeasyspringangular.controller.secure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

  // Secured Endpoint for testing (having a valid JWT)
  @GetMapping("/test")
  public ResponseEntity<String> testSecured() {
    return ResponseEntity.ok("You reached a secured endpoint with a valid JWT!");
  }

}

