import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponseDTO } from '../models/page-response.model';
import { ReviewDTO } from '../models/property.models';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  constructor(private http: HttpClient) {}

  upsertReview(propertyId: number, dto: { rating: number; comment: string }) {
    return this.http.put(`/api/properties/${propertyId}/reviews`, dto);
  }

  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`/api/reviews/${reviewId}`);
  }

  getPagedReviews(
    propertyId: number,
    page: number,
    size: number,
    sortBy: string,
    direction: string
  ): Observable<PageResponseDTO<ReviewDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<PageResponseDTO<ReviewDTO>>(
      `/api/properties/${propertyId}/reviews/paged`,
      { params }
    );
  }
}
