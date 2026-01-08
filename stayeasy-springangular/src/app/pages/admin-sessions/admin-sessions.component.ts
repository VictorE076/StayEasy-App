import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { AdminAuditService } from '../../service/admin-audit.service';
import { SessionAuditDTO } from '../../models/session-audit.dto';

type StatusFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';

@Component({
  selector: 'app-admin-sessions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-sessions.component.html',
  styleUrls: ['./admin-sessions.component.css']
})
export class AdminSessionsComponent implements OnInit {

  loading: boolean = false;
  errorMsg: string = '';

  sessions: SessionAuditDTO[] = [];

  // UI state
  q: string = '';
  status: StatusFilter = 'ALL';
  sortBy: 'createdAt' | 'lastActivity' | 'username' = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';

  constructor(
    private auditService: AdminAuditService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  goToHomePage(): void {
    this.router.navigate(['/homepage']);
  }

  load(): void {
    this.loading = true;
    this.errorMsg = '';

    this.auditService.getSessionsAudit().subscribe({
      next: (data: SessionAuditDTO[]) => {
        this.sessions = data ?? [];
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;

        if (err.status === 401) {
          this.errorMsg = 'Neautorizat (401). Token invalid/expirat.';
        } else if (err.status === 403) {
          this.errorMsg = 'Interzis (403). Nu ai drepturi pentru acest endpoint.';
        } else {
          this.errorMsg = 'Eroare la încărcarea sesiunilor.';
        }
      }
    });
  }

  setSort(col: 'createdAt' | 'lastActivity' | 'username'): void {
    if (this.sortBy === col) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = col;
      this.sortDir = 'asc';
    }
  }

  get filtered(): SessionAuditDTO[] {
    const query = this.q.trim().toLowerCase();
    let out: SessionAuditDTO[] = [...this.sessions];

    // filter status
    if (this.status === 'ACTIVE') out = out.filter((s: SessionAuditDTO) => s.active);
    if (this.status === 'INACTIVE') out = out.filter((s: SessionAuditDTO) => !s.active);

    // search
    if (query) {
      out = out.filter((s: SessionAuditDTO) =>
        (s.username ?? '').toLowerCase().includes(query) ||
        (s.sessionId ?? '').toLowerCase().includes(query)
      );
    }

    // sort
    const dirMul: number = this.sortDir === 'asc' ? 1 : -1;

    out.sort((a: SessionAuditDTO, b: SessionAuditDTO) => {
      if (this.sortBy === 'username') {
        return dirMul * (a.username ?? '').localeCompare(b.username ?? '');
      }

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
