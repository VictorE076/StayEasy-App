package com.stayeasy.stayeasyspringangular.Service;

import com.stayeasy.stayeasyspringangular.DTO.SessionAuditDTO;
import com.stayeasy.stayeasyspringangular.Repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stayeasy.stayeasyspringangular.DTO.PageResponseDTO;
import com.stayeasy.stayeasyspringangular.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class AdminAuditService {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(AdminAuditService.class);

  private final UserSessionRepository userSessionRepository;

  public AdminAuditService(UserSessionRepository userSessionRepository) {
    this.userSessionRepository = userSessionRepository;
  }

  public List<SessionAuditDTO> getAllUserSessions() {
    List<SessionAuditDTO> sessions = userSessionRepository.findAllSessionsForAudit();

    logger.info("Admin audit loaded {} user sessions", sessions.size());

    return sessions;
  }

  public PageResponseDTO<SessionAuditDTO> getAllUserSessionsPaged(
    int page, int size, String sortBy, String direction
  ) {
    if (page < 0) {
      throw new BadRequestException("Page number cannot be negative");
    }

    if (size < 1 || size > 50) {
      throw new BadRequestException("Page size must be between 1 and 50");
    }

    List<String> allowedSortFields = List.of(
      "id",
      "username",
      "createdAt",
      "lastActivity",
      "active"
    );

    if (!allowedSortFields.contains(sortBy)) {
      throw new BadRequestException("Invalid sort field: " + sortBy);
    }

    String entitySortField = sortBy.equals("username") ? "user.username" : sortBy;

    Sort sort;

    if ("desc".equalsIgnoreCase(direction)) {
      sort = Sort.by(entitySortField).descending();
    } else if ("asc".equalsIgnoreCase(direction)) {
      sort = Sort.by(entitySortField).ascending();
    } else {
      throw new BadRequestException("Sort direction must be asc or desc");
    }

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<SessionAuditDTO> sessionPage = userSessionRepository.findAllSessionsForAudit(pageable);

    logger.info("Admin audit loaded page {} with size {}, total sessions {}", page, size, sessionPage.getTotalElements());

    return PageResponseDTO.<SessionAuditDTO>builder()
      .content(sessionPage.getContent())
      .pageNumber(sessionPage.getNumber())
      .pageSize(sessionPage.getSize())
      .totalElements(sessionPage.getTotalElements())
      .totalPages(sessionPage.getTotalPages())
      .first(sessionPage.isFirst())
      .last(sessionPage.isLast())
      .sortBy(sortBy)
      .direction(direction.toLowerCase())
      .build();
  }

}
