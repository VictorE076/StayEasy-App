import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessionAuditDTO } from '../models/session-audit.dto';
import { PageResponseDTO } from '../models/page-response.model';

@Injectable({ providedIn: 'root' })
export class AdminAuditService {
  private readonly API_URL = '/api/admin/audit';

  constructor(private http: HttpClient) {}

  getSessionsAudit(): Observable<SessionAuditDTO[]> {
    return this.http.get<SessionAuditDTO[]>(`${this.API_URL}/sessions`);
  }

  getPagedSessionsAudit(
    page: number,
    size: number,
    sortBy: string,
    direction: string
  ): Observable<PageResponseDTO<SessionAuditDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<PageResponseDTO<SessionAuditDTO>>(
      `${this.API_URL}/sessions/paged`,
      { params }
    );
  }
}
