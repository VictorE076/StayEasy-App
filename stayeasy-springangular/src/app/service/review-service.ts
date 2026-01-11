import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Review {
  id?: number;
  rating: number;
  comment: string;
  userId?: number;
  propertyId?: number;
  userName?: string;
  createdAt?: string;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  submitReview(review: Review): Observable<any> {
    return this.http.post(this.apiUrl, review);
  }

  getReviews(propertyId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/property/${propertyId}`);
  }
}
