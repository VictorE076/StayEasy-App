import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ReviewDTO} from '../models/reviewDTO';

@Injectable({ providedIn: 'root' })
export class ReviewService {

  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getReviews(propertyId: number) {
    return this.http.get<any[]>(
      `${this.api}/properties/${propertyId}/reviews`
    );
  }

  createOrUpdate(propertyId: number, payload: { rating: number; comment: string }) {
    return this.http.put(
      `${this.api}/properties/${propertyId}/reviews`,
      payload
    );
  }

  getSummary(propertyId: number) {
    return this.http.get<any>(
      `${this.api}/properties/${propertyId}/reviews/summary`
    );
  }

  deleteReview(reviewId: number) {
    return this.http.delete(
      `${this.api}/reviews/${reviewId}`
    );
  }
}
