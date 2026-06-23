export interface PageResponseDTO<T> {
  content: T[];

  pageNumber: number;
  pageSize: number;

  totalElements: number;
  totalPages: number;

  first: boolean;
  last: boolean;

  sortBy: string;
  direction: string;
}
