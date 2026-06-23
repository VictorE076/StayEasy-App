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
  createdAt?: string | null;
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
  amenityNames?: string[];
  houseRules?: HouseRulesRequestDTO | null;
}

export interface HouseRulesRequestDTO {
  smokingAllowed: boolean;
  petsAllowed: boolean;
  checkInTime?: string | null;
  checkOutTime?: string | null;
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
  propertyId?: number;
  rating: number;
  comment: string;
  userName?: string;
  username?: string;
  createdAt: string;
}

export interface HouseRulesDTO {
  id?: number;
  smokingAllowed: boolean;
  petsAllowed: boolean;
  checkInTime?: string | null;
  checkOutTime?: string | null;
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
  createdAt?: string | null;
  images: string[];
  amenities: AmenityDTO[];
  reviews: ReviewDTO[];
  houseRules: HouseRulesDTO | null;
  availability: AvailabilityDTO[];
  averageRating: number | null;
  totalReviews: number;
}
