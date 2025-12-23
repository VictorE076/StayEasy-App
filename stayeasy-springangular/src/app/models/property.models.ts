export enum PropertyType {
  APARTMENT = 'APARTMENT',
  HOUSE = 'HOUSE',
  STUDIO = 'STUDIO',
  VILLA = 'VILLA'
}

// Response DTO - matches backend PropertyResponseDTO
export interface PropertyResponseDTO {
  id: number;
  title: string;
  description: string;
  city: string;
  address: string;
  pricePerNight: number;
  maxGuests: number;
  propertyType: string;
  ownerUsername: string;
  images: string[];
}

// Request DTO - matches backend PropertyRequestDTO
export interface PropertyRequestDTO {
  title: string;
  city: string;
  address: string;
  description?: string;
  pricePerNight: number;
  maxGuests: number;
  propertyType: PropertyType;
  imagePaths?: string[];
}

// Search params
export interface PropertySearchParams {
  city?: string;
  maxPrice?: number;
}
