import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginDTO} from '../models/loginDTO';
import { RegisterDTO } from '../models/registerDTO';
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

  logout(sessionId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/logout?sessionId=${sessionId}`, {});
  }

  register(data: RegisterDTO): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, data, { responseType: 'text' });
  }

}


