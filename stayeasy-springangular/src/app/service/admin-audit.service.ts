import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessionAuditDTO } from '../models/session-audit.dto';

// Ajustează asta dacă ai deja un environment.apiUrl
const API_BASE = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class AdminAuditService {
  constructor(private http: HttpClient) {}

  getSessionsAudit(): Observable<SessionAuditDTO[]> {
    // corespunde @RequestMapping("/api/admin/audit") + @GetMapping("/sessions") :contentReference[oaicite:2]{index=2}
    return this.http.get<SessionAuditDTO[]>(`${API_BASE}/api/admin/audit/sessions`);
  }
}
