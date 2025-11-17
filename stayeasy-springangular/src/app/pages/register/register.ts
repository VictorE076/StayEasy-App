import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {RegisterDTO} from '../../models/registerDTO';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule
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

  constructor(private router: Router) {}

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

    // TODO: Implement actual registration when backend is ready
    // this.registerService.register(this.userToRegister).subscribe({...});

    // Simulate registration for now
    setTimeout(() => {
      this.isLoading = false;
      this.successMessage = 'Registration functionality will be available soon!';
      console.log('Registration data prepared:', this.userToRegister);
    }, 1000);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
