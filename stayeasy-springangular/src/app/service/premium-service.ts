import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PremiumStatusDTO {
  premium: boolean;
  planName: string;
  price: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class PremiumService {
  private apiUrl = '/api/premium';

  constructor(private http: HttpClient) {}

  getStatus(): Observable<PremiumStatusDTO> {
    return this.http.get<PremiumStatusDTO>(`${this.apiUrl}/status`);
  }

  activateDemo(): Observable<PremiumStatusDTO> {
    return this.http.post<PremiumStatusDTO>(`${this.apiUrl}/activate-demo`, {});
  }

  deactivateDemo(): Observable<PremiumStatusDTO> {
    return this.http.post<PremiumStatusDTO>(`${this.apiUrl}/deactivate-demo`, {});
  }
}
