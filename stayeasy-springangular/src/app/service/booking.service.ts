import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private readonly apiUrl = '/api/bookings';

  constructor(private http: HttpClient) {}

  bookNow(propertyId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/book-now/${propertyId}`, {}, { responseType: 'text' });
  }
}
