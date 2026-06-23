import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../service/auth-service';

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

  q: string = '';
  status: StatusFilter = 'ALL';

  pageNumber: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;
  totalPages: number = 0;

  sortBy: 'createdAt' | 'lastActivity' | 'username' | 'active' = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';

  constructor(
    private auditService: AdminAuditService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    if (!this.authService.getToken()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.authService.isAdmin()) {
      this.router.navigate(['/homepage']);
      return;
    }

    this.load();
  }

  goToHomePage(): void {
    this.router.navigate(['/homepage']);
  }

  load(): void {
    this.loading = true;
    this.errorMsg = '';

    this.auditService.getPagedSessionsAudit(
      this.pageNumber,
      this.pageSize,
      this.sortBy,
      this.sortDir
    ).subscribe({
      next: (response) => {
        this.sessions = response.content ?? [];
        this.pageNumber = response.pageNumber;
        this.pageSize = response.pageSize;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.sortBy = response.sortBy as any;
        this.sortDir = response.direction as any;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;

        if (err.status === 401) {
          this.errorMsg = 'Unauthorized (401). Token invalid or expired.';
        } else if (err.status === 403) {
          this.errorMsg = 'Forbidden (403). You do not have admin rights for this endpoint.';
        } else {
          this.errorMsg = 'Error loading sessions.';
        }
      }
    });
  }

  setSort(col: 'createdAt' | 'lastActivity' | 'username' | 'active'): void {
    if (this.sortBy === col) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = col;
      this.sortDir = 'asc';
    }

    this.pageNumber = 0;
    this.load();
  }

  onPageSizeChanged(): void {
    this.pageNumber = 0;
    this.load();
  }

  goToPreviousPage(): void {
    if (this.pageNumber > 0) {
      this.pageNumber--;
      this.load();
    }
  }

  goToNextPage(): void {
    if (this.pageNumber < this.totalPages - 1) {
      this.pageNumber++;
      this.load();
    }
  }

  get filtered(): SessionAuditDTO[] {
    const query = this.q.trim().toLowerCase();

    let out: SessionAuditDTO[] = [...this.sessions];

    if (this.status === 'ACTIVE') out = out.filter((s) => s.active);
    if (this.status === 'INACTIVE') out = out.filter((s) => !s.active);

    if (query) {
      out = out.filter((s) =>
        (s.username ?? '').toLowerCase().includes(query) ||
        (s.sessionId ?? '').toLowerCase().includes(query)
      );
    }

    return out;
  }

  formatDate(iso: string): string {
    if (!iso) return '-';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
  }

  get totalCurrentPage(): number {
    return this.filtered.length;
  }
}
