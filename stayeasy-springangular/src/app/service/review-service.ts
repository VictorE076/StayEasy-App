import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  constructor(private http: HttpClient) {}

  upsertReview(propertyId: number, dto: { rating: number; comment: string }) {
    return this.http.put(`/api/properties/${propertyId}/reviews`, dto);
  }

  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`/api/reviews/${reviewId}`);
  }

}
