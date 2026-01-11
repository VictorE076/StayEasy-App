package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Service.AdminAuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")

public class AdminAuditController {

  private final AdminAuditService adminAuditService;

  public AdminAuditController(AdminAuditService adminAuditService) {
    this.adminAuditService = adminAuditService;
  }

  @GetMapping("/sessions")
  @PreAuthorize("hasRole('ADMIN')")
  public List<SessionAuditDTO> getSessionsAudit() {
    return adminAuditService.getAllUserSessions();
  }

}
