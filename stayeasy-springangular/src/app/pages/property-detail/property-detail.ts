import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {PropertyDetailDTO} from '../../models/property.models';
import {ActivatedRoute, Router} from '@angular/router';
import {PropertyService} from '../../service/property-service';
import {finalize} from 'rxjs/operators';
import { AuthService } from '../../service/auth-service';
import { ReviewService } from '../../service/review-service';
import { ReviewDTO } from '../../models/property.models';
import { BookingService, LoyaltyStatus } from '../../service/booking.service';
import {CreatePropertyModal} from '../create-property-modal/create-property-modal';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, CreatePropertyModal],
  templateUrl: './property-detail.html',
  styleUrl: './property-detail.css',
})
export class PropertyDetail {
  property: PropertyDetailDTO | null = null;
  isLoading = false;
  error: string | null = null;
  currentImageIndex = 0;
  loyalty: LoyaltyStatus | null = null;
  aiSummary: string | null = null;
  isAiLoading = false;
  aiError: string | null = null;
  reviews: ReviewDTO[] = [];
  reviewsLoading = false;
  reviewsError: string | null = null;
  reviewPage = 0;
  reviewPageSize = 3;
  reviewTotalPages = 0;
  reviewTotalElements = 0;
  reviewSortBy = 'createdAt';
  reviewDirection = 'desc';
  showEditPropertyModal = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService,
    private reviewService: ReviewService,
    private bookingService: BookingService
  ) {}

  generateAiSummary(): void {
    if (!this.property) return;

    this.isAiLoading = true;
    this.aiError = null;
    this.aiSummary = null;

    this.propertyService.getPropertyAiSummary(this.property.id)
      .pipe(finalize(() => this.isAiLoading = false))
      .subscribe({
        next: (summary) => {
          this.aiSummary = summary;
        },
        error: (err) => {
          console.error('Error fetching AI summary:', err);
          this.aiError = 'The AI summary could not be generated right now.';
        }
      });
  }

  loadReviewsPage(): void {
    if (!this.property) return;

    this.reviewsLoading = true;
    this.reviewsError = null;

    this.reviewService.getPagedReviews(
      this.property.id,
      this.reviewPage,
      this.reviewPageSize,
      this.reviewSortBy,
      this.reviewDirection
    )
      .pipe(finalize(() => this.reviewsLoading = false))
      .subscribe({
        next: (response) => {
          this.reviews = response.content;
          this.reviewPage = response.pageNumber;
          this.reviewPageSize = response.pageSize;
          this.reviewTotalPages = response.totalPages;
          this.reviewTotalElements = response.totalElements;
          this.reviewSortBy = response.sortBy;
          this.reviewDirection = response.direction;
        },
        error: (error) => {
          console.error('Error loading paged reviews:', error);
          this.reviewsError = 'Failed to load reviews.';
        }
      });
  }

  onReviewSortChanged(): void {
    this.reviewPage = 0;
    this.loadReviewsPage();
  }

  onReviewPageSizeChanged(): void {
    this.reviewPage = 0;
    this.loadReviewsPage();
  }

  goToPreviousReviewPage(): void {
    if (this.reviewPage > 0) {
      this.reviewPage--;
      this.loadReviewsPage();
    }
  }

  goToNextReviewPage(): void {
    if (this.reviewPage < this.reviewTotalPages - 1) {
      this.reviewPage++;
      this.loadReviewsPage();
    }
  }

  getReviewUsername(review: ReviewDTO): string {
    return review.userName || review.username || 'User';
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  canDeleteProperty(): boolean {
    if (!this.property) return false;
    const me = this.authService.getUsername();
    return this.isAdmin() || (!!me && this.property.ownerUsername === me);
  }

  canEditProperty(): boolean {
    return this.canDeleteProperty();
  }

  onEditProperty(): void {
    this.showEditPropertyModal = true;
  }

  onEditModalClose(): void {
    this.showEditPropertyModal = false;
  }

  onPropertySaved(): void {
    this.showEditPropertyModal = false;

    if (this.property) {
      this.loadPropertyDetail(this.property.id);
    }
  }

  onDeleteProperty(): void {
    if (!this.property) return;

    if (!confirm('Are you sure you want to delete this property?')) return;

    this.propertyService.deleteProperty(this.property.id).subscribe({
      next: () => this.router.navigate(['/homepage']),
      error: (err) => {
        console.error('Error deleting property:', err);
        alert('Failed to delete property.');
      }
    });
  }

  canDeleteReview(review: ReviewDTO): boolean {
    const me = this.authService.getUsername();
    return this.isAdmin() || (!!me && this.getReviewUsername(review) === me);
  }

  onDeleteReview(reviewId: number): void {
    if (!this.property) return;

    if (!confirm('Delete this review?')) return;

    this.reviewService.deleteReview(reviewId).subscribe({
      next: () => {
        this.loadReviewsPage();
        this.loadPropertyDetail(this.property!.id);
      },
      error: (err) => {
        console.error('Error deleting review:', err);
        alert('Failed to delete review.');
      }
    });
  }

  onBookNowClicked(): void {
    if (!this.property) return;

    const me = this.authService.getUsername();
    if (!me) {
      alert('You must be logged in to make a reservation.');
      return;
    }

    this.bookingService.bookNow(this.property.id).subscribe({
      next: (response) => {
        alert(response || 'Booking successful!');
        this.loadLoyaltyStatus();

        this.router.navigate(['/homepage']);
      },
      error: (err) => {
        console.error('Booking error:', err);
        alert(err.error || 'An error occurred while processing the reservation.');
      }
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPropertyDetail(+id);
    }

    if (this.authService.getUsername()) {
      this.loadLoyaltyStatus();
    }
  }

  loadLoyaltyStatus(): void {
    this.bookingService.getLoyaltyStatus().subscribe({
      next: (data) => {
        this.loyalty = data;
      },
      error: (err) => {
        console.error('Error loading loyalty status:', err);
      }
    });
  }

  onBookWithDiscountClicked(): void {
    if (!this.property) return;

    if (!confirm('Are you sure you want to use 5 coins for a 10% discount on this property?')) {
      return;
    }

    this.bookingService.bookWithDiscount(this.property.id).subscribe({
      next: (response) => {
        alert(response || 'Discount booking successful!');
        this.loadLoyaltyStatus();

        this.router.navigate(['/homepage']);
      },
      error: (err) => {
        console.error('Discount booking error:', err);
        alert(err.error || 'An error occurred while processing the discount reservation.');
      }
    });
  }

  loadPropertyDetail(id: number): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getPropertyDetailById(id)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (property) => {
          this.property = property;
          this.generateAiSummary();
          this.reviewPage = 0;
          this.loadReviewsPage();
        },
        error: (error) => {
          console.error('Error loading property:', error);
          this.error = 'Failed to load property details. Please try again.';
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/homepage']);
  }

  previousImage(): void {
    if (this.property && this.property.images.length > 0) {
      this.currentImageIndex =
        (this.currentImageIndex - 1 + this.property.images.length) % this.property.images.length;
    }
  }

  nextImage(): void {
    if (this.property && this.property.images.length > 0) {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.property.images.length;
    }
  }

  getCurrentImage(): string {
    if (this.property && this.property.images.length > 0) {
      return this.property.images[this.currentImageIndex];
    }
    return 'https://dummyimage.com/600x400/cccccc/000000&text=No+Image+Available';
  }

  getPropertyTypeLabel(type: string): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
  }

  formatDate(dateString?: string | null): string {
    if (!dateString) {
      return 'Not available';
    }

    const date = new Date(dateString);

    if (isNaN(date.getTime())) {
      return 'Not available';
    }

    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getStarArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < rating ? 1 : 0);
  }

  goToReviewForm(): void {
    if (!this.property) return;
    this.router.navigate(['/properties', this.property.id, 'review']);
  }

}
