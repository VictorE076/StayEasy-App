import { Component, OnInit } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { LoginService } from '../../service/login-service';
import { AuthService } from '../../service/auth-service';
import { PropertyResponseDTO } from '../../models/property.models';
import {PropertyService} from '../../service/property-service';
import {CreatePropertyModal} from '../create-property-modal/create-property-modal';
import { UserADMIN_DTO } from '../../models/user-admin.dto';

import {FormsModule} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  imports: [NgIf, NgForOf, CreatePropertyModal, FormsModule],
  templateUrl: './homepage.html',
  styleUrl: './homepage.css',
  standalone: true
})
export class Homepage implements OnInit {
  userName: string = '';
  userEmail: string = '';
  userId: number = 0;
  isLoggingOut: boolean = false;
  showUserMenu: boolean = false;
  showCreateModal: boolean = false;
  user: UserADMIN_DTO | null = null;
  properties: PropertyResponseDTO[] = [];
  isLoading: boolean = false;
  error: string | null = null;
  userRole: string | null = null;
  searchCity: string = '';
  searchMaxPrice: number | null = null;
  isSearching: boolean = false;

  constructor(
    private authService: AuthService,
    private loginService: LoginService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadProperties();
  }

  loadUserInfo(): void {
    const token = this.authService.getToken();

    console.log('[DEBUG] token exists:', !!token);

    if (!token) {
      this.userRole = null;
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));

      console.log('[DEBUG] JWT payload:', payload);
      console.log('[DEBUG] payload.role:', payload.role);
      console.log('[DEBUG] payload.roles:', payload.roles);
      console.log('[DEBUG] payload.authorities:', payload.authorities);

      this.userName = payload.name || payload.sub || 'User';
      this.userEmail = payload.email || '';
      this.userId = payload.userId || payload.id || 0;

      // Rol - strict din payload.role
      this.userRole = payload.role || null;

      console.log('[DEBUG] userRole set to: ', this.userRole);

    } catch (error) {
      console.error('[DEBUG] Error parsing token:', error);
      this.userName = 'User';
      this.userRole = null;
    }
  }



  goToAdminSessions(): void {
    this.router.navigate(['/admin/sessions']);
  }

  loadProperties(): void {
    this.isLoading = true;
    this.error = null;

    this.propertyService.getAllProperties()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (properties) => {
          this.properties = properties;
        },
        error: (error) => {
          console.error('Error loading properties:', error);
          this.error = 'Failed to load properties. Please try again later.';
        }
      });
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
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

  getInitials(): string {
    return this.userName.charAt(0).toUpperCase();
  }

  onBecomeHost(): void {
    this.showCreateModal = true;
  }

  onModalClose(): void {
    this.showCreateModal = false;
  }

  onPropertyCreated(): void {
    this.loadProperties();
  }

  getMainImage(property: PropertyResponseDTO): string {
    return property.images && property.images.length > 0
      ? property.images[0]
      : 'https://dummyimage.com/600x400/cccccc/000000&text=No+Image+Available';
  }

  getPropertyTypeLabel(type: string): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
  }

  isOwner(property: PropertyResponseDTO): boolean {
    return this.userName != null && property.ownerUsername === this.userName;
  }

  onDeleteProperty(propertyId: number): void {
    if (!confirm('Are you sure you want to delete this property?')) {
      return;
    }

    this.propertyService.deleteProperty(propertyId)
      .subscribe({
        next: () => {
          this.loadProperties();
        },
        error: (error) => {
          console.error('Error deleting property:', error);
          alert('Failed to delete property. Please try again.');
        }
      });
  }

  onSearch(): void {
    this.isSearching = true;
    this.error = null;

    const city = this.searchCity.trim() || undefined;
    const maxPrice = this.searchMaxPrice && this.searchMaxPrice > 0 ? this.searchMaxPrice : undefined;

    this.propertyService.searchProperties(city, maxPrice)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe({
        next: (properties) => {
          this.properties = properties;
        },
        error: (error) => {
          console.error('Error searching properties:', error);
          this.error = 'Failed to search properties. Please try again.';
        }
      });
  }

  onClearSearch(): void {
    this.searchCity = '';
    this.searchMaxPrice = null;
    this.loadProperties();
  }
}
