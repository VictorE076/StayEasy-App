import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginDTO} from '../models/loginDTO';
import {Observable} from 'rxjs';
export interface AuthResponse {
  token: string;
}
@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: LoginDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials);
  }

  logout(sessionId: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/logout?sessionId=${sessionId}`, {});
  }
}
