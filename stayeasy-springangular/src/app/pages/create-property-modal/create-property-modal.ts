import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {PropertyRequestDTO, PropertyType} from '../../models/property.models';
import {PropertyService} from '../../service/property-service';
import {finalize} from 'rxjs/operators';

@Component({
  selector: 'app-create-property-modal',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-property-modal.html',
  styleUrl: './create-property-modal.css',
  standalone: true
})
export class CreatePropertyModal {
  @Input() currentUserId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() propertyCreated = new EventEmitter<void>();

  propertyTypes = Object.values(PropertyType);
  isSubmitting = false;
  error: string | null = null;

  property: PropertyRequestDTO = {
    title: '',
    city: '',
    address: '',
    description: '',
    pricePerNight: 0,
    maxGuests: 1,
    propertyType: PropertyType.APARTMENT,
    imagePaths: []
  };

  imagePathInput = '';

  constructor(private propertyService: PropertyService) {}

  onClose(): void {
    if (!this.isSubmitting) {
      this.close.emit();
    }
  }

  addImagePath(): void {
    if (this.imagePathInput.trim()) {
      if (!this.property.imagePaths) {
        this.property.imagePaths = [];
      }
      if (this.property.imagePaths.length < 20) {
        this.property.imagePaths.push(this.imagePathInput.trim());
        this.imagePathInput = '';
      }
    }
  }

  removeImagePath(index: number): void {
    this.property.imagePaths?.splice(index, 1);
    console.log(this.currentUserId);
  }

  onSubmit(): void {
    this.error = null;

    if (!this.validateForm()) {
      return;
    }

    // this.property.ownerId = this.currentUserId;
    this.isSubmitting = true;

    this.propertyService.createProperty(this.property)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: () => {
          this.propertyCreated.emit();
          this.close.emit();
        },
        error: (error) => {
          console.error('Error creating property:', error);
          this.error = error.error?.message || 'Failed to create property. Please try again.';
        }
      });
  }

  private validateForm(): boolean {
    if (!this.property.title.trim()) {
      this.error = 'Title is required';
      return false;
    }
    if (!this.property.city.trim()) {
      this.error = 'City is required';
      return false;
    }
    if (!this.property.address.trim()) {
      this.error = 'Address is required';
      return false;
    }
    if (this.property.pricePerNight <= 0) {
      this.error = 'Price must be greater than 0';
      return false;
    }
    if (this.property.maxGuests < 1 || this.property.maxGuests > 50) {
      this.error = 'Max guests must be between 1 and 50';
      return false;
    }
    return true;
  }
}
