import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterDTO } from '../../models/registerDTO';
import { LoginService } from '../../service/login-service';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ApiErrorService } from '../../service/api-error.service';
import {NgIf} from '@angular/common';
// import { AuthService } from '../../service/auth-service';


@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './register.html',
  styleUrl: './register.css',
  standalone: true
})
export class Register {
  userToRegister: RegisterDTO = {
    username: '',
    email: '',
    password: '',
    fullName: ''
  };

  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private apiErrorService: ApiErrorService
  ) {}

  register(): void {
    if (!this.userToRegister.username || !this.userToRegister.email ||
      !this.userToRegister.password || !this.userToRegister.fullName) {
      this.errorMessage = 'Please fill in all fields';
      this.successMessage = '';
      return;
    }

    if (this.userToRegister.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      this.successMessage = '';
      return;
    }

    if (this.userToRegister.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      this.successMessage = '';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    /// TEST Register (See browser's console)
    // console.log('Register clicked', this.userToRegister);
    ///

    this.loginService.register(this.userToRegister)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (response) => {
          this.successMessage = response.message || 'Account created successfully';
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.errorMessage = this.apiErrorService.getMessage(
            error,
            'Register failed. Please try again.'
          );
        },
      complete: () => {
        this.isLoading = false;
      }
    });

    // Simulate registration for now
    // setTimeout(() => {
    //   this.isLoading = false;
    //   this.successMessage = 'Registration functionality will be available soon!';
    //   console.log('Registration data prepared:', this.userToRegister);
    // }, 1000);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
