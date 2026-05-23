import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoyaltyStatus {
  completedBookings: number;
  loyaltyCoins: number;
  bookingsUntilNextCoin: number;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private readonly apiUrl = '/api/bookings';

  constructor(private http: HttpClient) {}

  bookNow(propertyId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/book-now/${propertyId}`, {}, { responseType: 'text' });
  }

  getLoyaltyStatus(): Observable<LoyaltyStatus> {
    return this.http.get<LoyaltyStatus>(`${this.apiUrl}/my-loyalty`);
  }

}
