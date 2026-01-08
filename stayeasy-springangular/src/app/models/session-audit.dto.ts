export interface SessionAuditDTO {
  sessionId: string;
  username: string;
  createdAt: string;     // vine din backend ca ISO (LocalDateTime serializat)
  lastActivity: string;  // ISO
  active: boolean;
}
