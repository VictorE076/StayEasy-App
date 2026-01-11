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


export interface AmenityDTO {
  id: number;
  name: string;
}

export interface ReviewDTO {
  id: number;
  rating: number;
  comment: string;
  userName: string;
  createdAt: string;
}

export interface HouseRulesDTO {
  id: number;
  smokingAllowed: boolean;
  petsAllowed: boolean;
  checkInTime: string;
  checkOutTime: string;
}

export interface AvailabilityDTO {
  id: number;
  availableFrom: string;
  availableTo: string;
}

export interface PropertyDetailDTO {
  id: number;
  title: string;
  description: string;
  city: string;
  address: string;
  pricePerNight: number;
  maxGuests: number;
  propertyType: string;
  ownerUsername: string;
  createdAt: string;
  images: string[];
  amenities: AmenityDTO[];
  reviews: ReviewDTO[];
  houseRules: HouseRulesDTO | null;
  availability: AvailabilityDTO[];
  averageRating: number | null;
  totalReviews: number;
}
