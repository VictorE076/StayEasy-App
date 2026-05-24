package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.PremiumStatusDTO;
import com.stayeasy.stayeasyspringangular.Service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PremiumController {

  private final PremiumService premiumService;

  @GetMapping("/status")
  public PremiumStatusDTO getStatus() {
    return premiumService.getPremiumStatus();
  }

  @PostMapping("/activate-demo")
  public PremiumStatusDTO activateDemoPremium() {
    return premiumService.activateDemoPremium();
  }

  @PostMapping("/deactivate-demo")
  public PremiumStatusDTO deactivateDemoPremium() {
    return premiumService.deactivateDemoPremium();
  }
}
