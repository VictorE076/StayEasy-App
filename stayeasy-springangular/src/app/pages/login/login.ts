import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {LoginDTO} from '../../models/loginDTO';
import {AuthService} from '../../service/auth-service';
import {Router} from '@angular/router';
import {LoginService} from '../../service/login-service';
import { ApiErrorService } from '../../service/api-error.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
  standalone: true
})
export class Login {
  credentials: LoginDTO = {
    username: '',
    password: '',
    rememberMe: false
  };

  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private loginService: LoginService,
    private authService: AuthService,
    private router: Router,
    private apiErrorService: ApiErrorService
  ) {}

  onSubmit(): void {
    if (!this.credentials.username || !this.credentials.password) {
      this.errorMessage = 'Please enter both username and password';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.loginService.login(this.credentials).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.router.navigate(['/homepage']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = this.apiErrorService.getMessage(
          error,
          'Login failed. Please check your credentials.'
        );
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  onRegister(): void {
    this.router.navigate(['/register']);
  }
}
