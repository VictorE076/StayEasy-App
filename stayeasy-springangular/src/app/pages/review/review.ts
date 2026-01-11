import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../../service/review-service';
import { ReviewRequest, ReviewResponse} from '../../models/reviewDTO';
import { AuthService } from '../../service/auth-service';
import { LoginService } from '../../service/login-service';
import {DecimalPipe, DatePipe, CommonModule} from '@angular/common';
import {finalize} from 'rxjs/operators';
import {NgForOf,NgIf} from '@angular/common';

@Component({
  selector: 'app-review',
  templateUrl: './review.html',
  styleUrls: ['./review.css'],
  standalone: true,
  imports: [NgForOf, NgIf, FormsModule, DecimalPipe, DatePipe,CommonModule]
})
export class ReviewComponent implements OnInit {
  @Input({ required: true }) propertyId!: number;

  userName: string = '';
  userEmail: string = '';
  userId: number = 0;

  rating: number = 0;
  maxStars: number = 5;
  reviewText: string = '';
  stars: number[] = [];

  reviews: any[] = [];
  loadingReviews: boolean = false;
  averageRating: number = 0;

  isLoggingOut: boolean = false;
  showUserMenu: boolean = false;
  router: any;

  constructor(
    private authService: AuthService,
    private loginService: LoginService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.stars = Array.from({ length: this.maxStars }, (_, i) => i + 1);

    if (this.propertyId) {
      this.loadReviews();
    }
  }

  loadUserInfo(): void {
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userName = payload.name || payload.sub || 'User';
        this.userEmail = payload.email || '';
        this.userId = payload.userId || payload.id || 0;
      } catch {
        this.userName = 'User';
      }
    }
  }
  onLogout(): void {
    this.isLoggingOut = true;
    const token = this.authService.getToken();

    if (!token) {
      this.authService.logout();
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const sessionId = payload.sid;

      if (sessionId) {
        this.loginService.logout(sessionId)
          .pipe(finalize(() => this.isLoggingOut = false))
          .subscribe({
            next: () => {
              this.authService.logout();
            },
            error: (error) => {
              console.error('Logout error:', error);
              this.authService.logout();
            }
          });
      } else {
        this.authService.logout();
        this.isLoggingOut = false;
      }
    } catch (error) {
      console.error('Token parsing error:', error);
      this.authService.logout();
      this.isLoggingOut = false;
    }
  }
  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }
  onBack() {
    this.router.navigate(['/homepage']);
  }


  getInitials(): string {
    return this.userName.charAt(0).toUpperCase();
  }
  setRating(value: number): void {
    this.rating = value;
  }

  submitReview(): void {
    if (this.rating === 0 || !this.reviewText.trim()) return;

    const payload: ReviewRequest = {
      rating: this.rating,
      comment: this.reviewText,
      userId: this.userId,
      propertyId: this.propertyId
    };

    this.reviewService.submitReview(payload).subscribe({
      next: () => {
        // reset form
        this.rating = 0;
        this.reviewText = '';
        this.loadReviews();
      },
      error: (err: any) => console.error(err)
    });
  }
  loadReviews(): void {
    this.loadingReviews = true;
    this.reviewService.getReviews(this.propertyId).subscribe({
      next: (res: ReviewResponse[]) => {
        this.reviews = res;
        this.loadingReviews = false;
        this.calculateAverageRating();
      },
      error: (err: ReviewResponse) => {
        console.error('Failed to load reviews', err);
        this.loadingReviews = false;
      }
    });
  }

  calculateAverageRating(): void {
    if (this.reviews.length === 0) {
      this.averageRating = 0;
      return;
    }
    const sum = this.reviews.reduce((acc, r) => acc + r.rating, 0);
    this.averageRating = sum / this.reviews.length;
  }
}

