import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {PropertyResponseDTO, PropertyRequestDTO, PropertyDetailDTO} from '../models/property.models';
import { PageResponseDTO } from '../models/page-response.model';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private readonly API_URL = '/api/properties';

  constructor(private http: HttpClient) {}

  getAllProperties(): Observable<PropertyResponseDTO[]> {
    return this.http.get<PropertyResponseDTO[]>(this.API_URL);
  }

  getPagedProperties(
    page: number, size: number, sortBy: string, direction: string
  ): Observable<PageResponseDTO<PropertyResponseDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<PageResponseDTO<PropertyResponseDTO>>(
      `${this.API_URL}/paged`,
      { params }
    );
  }

  getPropertyById(id: number): Observable<PropertyResponseDTO> {
    return this.http.get<PropertyResponseDTO>(`${this.API_URL}/${id}`);
  }

  getPropertyDetailById(id: number): Observable<PropertyDetailDTO> {
    return this.http.get<PropertyDetailDTO>(`${this.API_URL}/${id}/details`);
  }

  searchProperties(city?: string, maxPrice?: number): Observable<PropertyResponseDTO[]> {
    let params = new HttpParams();

    if (city) {
      params = params.set('city', city);
    }

    if (maxPrice !== undefined && maxPrice !== null) {
      params = params.set('maxPrice', maxPrice.toString());
    }

    return this.http.get<PropertyResponseDTO[]>(`${this.API_URL}/search`, { params });
  }

  createProperty(property: PropertyRequestDTO): Observable<PropertyResponseDTO> {
    return this.http.post<PropertyResponseDTO>(this.API_URL, property);
  }

  updateProperty(id: number, property: PropertyRequestDTO): Observable<PropertyResponseDTO> {
    return this.http.put<PropertyResponseDTO>(`${this.API_URL}/${id}`, property);
  }

  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getPropertyAiSummary(propertyId: number): Observable<string> {
    return this.http.get(`${this.API_URL}/${propertyId}/ai-summary`, {
      responseType: 'text'
    });
  }

}
