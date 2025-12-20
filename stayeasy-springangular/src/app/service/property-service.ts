import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PropertyResponseDTO, PropertyRequestDTO } from '../models/property.models';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private readonly API_URL = '/api/properties';

  constructor(private http: HttpClient) {}

  getAllProperties(): Observable<PropertyResponseDTO[]> {
    return this.http.get<PropertyResponseDTO[]>(this.API_URL);
  }

  getPropertyById(id: number): Observable<PropertyResponseDTO> {
    return this.http.get<PropertyResponseDTO>(`${this.API_URL}/${id}`);
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

  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
