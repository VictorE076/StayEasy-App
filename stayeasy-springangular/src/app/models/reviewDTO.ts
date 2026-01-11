export interface ReviewRequest {
  rating: number;
  comment: string;
  userId: number;
  propertyId: number;
}

export interface ReviewResponse {
  rating: number;
  comment: string;
  userName: string;
  createdAt: string;
}
