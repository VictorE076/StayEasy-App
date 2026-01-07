import { Component, OnInit } from '@angular/core';
import { AdminAuditService } from '../../service/admin-audit.service';
import { SessionAuditDTO } from '../../models/session-audit.dto';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

type StatusFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';

@Component({
  selector: 'app-admin-sessions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-sessions.component.html',
  styleUrls: ['./admin-sessions.component.css']
})
export class AdminSessionsComponent implements OnInit {

  loading = false;
  errorMsg = '';

  sessions: SessionAuditDTO[] = [];

  // UI state
  q = '';
  status: StatusFilter = 'ALL';
  sortBy: 'createdAt' | 'lastActivity' | 'username' = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';

  constructor(private auditService: AdminAuditService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMsg = '';

    this.auditService.getSessionsAudit().subscribe({
      next: (data) => {
        this.sessions = data ?? [];
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        if (err?.status === 401) {
          this.errorMsg = 'Neautorizat (401). Token invalid/expirat sau SESSION_EXPIRED.';
        } else if (err?.status === 403) {
          this.errorMsg = 'Interzis (403). Ai nevoie de ROLE_ADMIN.';
        } else {
          this.errorMsg = 'Eroare la încărcarea sesiunilor.';
        }
      }
    });
  }

  setSort(col: 'createdAt' | 'lastActivity' | 'username') {
    if (this.sortBy === col) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = col;
      this.sortDir = 'asc';
    }
  }

  get filtered(): SessionAuditDTO[] {
    const query = this.q.trim().toLowerCase();

    let out = this.sessions.slice();

    // filter status
    if (this.status === 'ACTIVE') out = out.filter(s => s.active);
    if (this.status === 'INACTIVE') out = out.filter(s => !s.active);

    // search
    if (query) {
      out = out.filter(s =>
        (s.username || '').toLowerCase().includes(query) ||
        (s.sessionId || '').toLowerCase().includes(query)
      );
    }

    // sort
    const dirMul = this.sortDir === 'asc' ? 1 : -1;

    out.sort((a, b) => {
      if (this.sortBy === 'username') {
        return dirMul * (a.username ?? '').localeCompare(b.username ?? '');
      }

      // createdAt / lastActivity as date
      const av = new Date((a as any)[this.sortBy]).getTime();
      const bv = new Date((b as any)[this.sortBy]).getTime();
      return dirMul * (av - bv);
    });

    return out;
  }

  formatDate(iso: string): string {
    if (!iso) return '-';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
  }

  get total(): number {
    return this.filtered.length;
  }
}
