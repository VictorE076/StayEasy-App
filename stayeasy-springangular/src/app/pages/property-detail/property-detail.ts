import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {PropertyDetailDTO} from '../../models/property.models';
import {ActivatedRoute, Router} from '@angular/router';
import {PropertyService} from '../../service/property-service';
import {finalize} from 'rxjs/operators';
import { AuthService } from '../../service/auth-service';
import { ReviewService } from '../../service/review-service';
import { ReviewDTO } from '../../models/property.models';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './property-detail.html',
  styleUrl: './property-detail.css',
})
export class PropertyDetail {
  property: PropertyDetailDTO | null = null;
  isLoading = false;
  error: string | null = null;
  currentImageIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService,
    private reviewService: ReviewService
  ) {}

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  canDeleteProperty(): boolean {
    if (!this.property) return false;
    const me = this.authService.getUsername();
    return this.isAdmin() || (!!me && this.property.ownerUsername === me);
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
    return this.isAdmin() || (!!me && review.userName === me);
  }

  onDeleteReview(reviewId: number): void {
    if (!this.property) return;

    if (!confirm('Delete this review?')) return;

    this.reviewService.deleteReview(reviewId).subscribe({
      next: () => this.loadPropertyDetail(this.property!.id), // refresh
      error: (err) => {
        console.error('Error deleting review:', err);
        alert('Failed to delete review.');
      }
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPropertyDetail(+id);
    }
  }

  loadPropertyDetail(id: number): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getPropertyDetailById(id)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (property) => {
          this.property = property;
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

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
  }

  getStarArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < rating ? 1 : 0);
  }

  goToReviewForm(): void {
    if (!this.property) return;
    this.router.navigate(['/properties', this.property.id, 'review']);
  }

}
