import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ReviewResponse, ReviewRequest} from '../models/reviewDTO';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  submitReview(review: ReviewRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, review);
  }

  getReviews(propertyId: number): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${this.apiUrl}/property/${propertyId}`);
  }
}
