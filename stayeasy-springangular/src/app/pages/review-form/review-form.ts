import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewService } from '../../service/review-service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.html',
  standalone: true,
  styleUrls: ['./review-form.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class ReviewFormComponent implements OnInit {
  propertyId!: number;
  isSubmitting = false;
  error: string | null = null;

  form!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private reviewService: ReviewService
  ) {
    this.form = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.maxLength(2000)]],
    });
  }

  ngOnInit(): void {
    this.propertyId = Number(this.route.snapshot.paramMap.get('id'));
  }

  submit(): void {
    if (this.form.invalid || this.isSubmitting) return;
    this.isSubmitting = true;
    this.error = null;

    const rating = Number(this.form.get('rating')?.value);
    const comment = String(this.form.get('comment')?.value ?? '').trim();

    this.reviewService.upsertReview(this.propertyId, { rating, comment }).subscribe({
      next: () => void this.router.navigate(['/property/', this.propertyId]),
      error: () => {
        this.error = 'Failed to submit review.';
        this.isSubmitting = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/property/', this.propertyId]);
  }
}
