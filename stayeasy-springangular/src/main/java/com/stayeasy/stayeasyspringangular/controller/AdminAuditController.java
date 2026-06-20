package com.stayeasy.stayeasyspringangular.controller;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Service.AdminAuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.stayeasy.stayeasyspringangular.DTO.PageResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/sessions/paged")
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponseDTO<SessionAuditDTO> getSessionsAuditPaged(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "5") int size,
    @RequestParam(defaultValue = "createdAt") String sortBy,
    @RequestParam(defaultValue = "desc") String direction
  ) {
    return adminAuditService.getAllUserSessionsPaged(page, size, sortBy, direction);
  }

}
