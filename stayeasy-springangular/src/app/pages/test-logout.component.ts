import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../service/auth-service';
import {LoginService} from '../service/login-service';

// This is just a placeholder page that tests logout.
// It will be deleted and replaced with actual pages of the app.

@Component({
  selector: 'app-test-logout',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="logout-container">
      <div class="logout-card">
        <div class="app-branding">
          <div class="app-icon">
            <i class="bi bi-house-heart-fill"></i>
          </div>
          <h1 class="app-name">StayEasy</h1>
        </div>

        <div class="content">
          <h2>You are logged in!</h2>
          <p>Token stored: <strong>{{ hasToken ? 'Yes' : 'No' }}</strong></p>

          <div *ngIf="hasToken" class="token-preview">
            <p class="token-label">Token (first 50 chars):</p>
            <code>{{ tokenPreview }}</code>
          </div>

          <button
            class="btn-logout"
            (click)="onLogout()"
            [disabled]="isLoading"
          >
            {{ isLoading ? 'Logging out...' : 'Logout' }}
          </button>

          <p *ngIf="message" class="message" [ngClass]="{'error': isError}">
            {{ message }}
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .logout-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .logout-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
      padding: 40px;
      width: 100%;
      max-width: 500px;
      position: relative;
    }

    .logout-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea, #764ba2);
      border-radius: 16px 16px 0 0;
    }

    .app-branding {
      text-align: center;
      margin-bottom: 32px;
      padding-bottom: 24px;
      border-bottom: 1px solid #f0f0f0;
    }

    .app-icon {
      width: 56px;
      height: 56px;
      margin: 0 auto 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      font-size: 28px;
    }

    .app-name {
      font-size: 28px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0;
      letter-spacing: -0.5px;
    }

    .content {
      text-align: center;
    }

    .content h2 {
      color: #333;
      font-size: 24px;
      font-weight: 600;
      margin: 0 0 20px 0;
    }

    .content p {
      color: #666;
      margin: 10px 0;
      font-size: 15px;
    }

    .token-preview {
      background: #f8f9fa;
      border: 1px solid #e8eaed;
      border-radius: 8px;
      padding: 15px;
      margin: 20px 0;
      text-align: left;
    }

    .token-label {
      font-size: 13px;
      color: #666;
      margin: 0 0 8px 0;
      font-weight: 500;
    }

    code {
      display: block;
      background: white;
      padding: 10px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      color: #667eea;
      word-break: break-all;
      border: 1px solid #e8eaed;
    }

    .btn-logout {
      width: 100%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      padding: 14px 20px;
      border-radius: 10px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      margin-top: 30px;
    }

    .btn-logout:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.35);
    }

    .btn-logout:disabled {
      opacity: 0.5;
      cursor: not-allowed;
      transform: none;
    }

    .message {
      margin-top: 15px;
      padding: 12px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      background-color: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }

    .message.error {
      background-color: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }

    @media (max-width: 480px) {
      .logout-card {
        padding: 32px 24px;
      }

      .app-name {
        font-size: 24px;
      }

      .content h2 {
        font-size: 20px;
      }
    }
  `]
})
export class TestLogoutComponent {
  hasToken: boolean = false;
  tokenPreview: string = '';
  isLoading: boolean = false;
  message: string = '';
  isError: boolean = false;

  constructor(
    private authService: AuthService,
    private loginService: LoginService
  ) {
    this.checkToken();
  }

  checkToken(): void {
    const token = this.authService.getToken();
    this.hasToken = !!token;
    if (token) {
      this.tokenPreview = token.substring(0, 50) + '...';
    }
  }

  onLogout(): void {
    this.isLoading = true;
    this.message = '';
    this.isError = false;

    // Extract session ID from token
    const token = this.authService.getToken();
    if (!token) {
      this.message = 'No token found';
      this.isError = true;
      this.isLoading = false;
      return;
    }

    // Decode JWT to get session ID (simple base64 decode)
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const sessionId = payload.sid;

      if (!sessionId) {
        this.message = 'Session ID not found in token';
        this.isError = true;
        this.isLoading = false;
        return;
      }

      // Call backend logout
      this.loginService.logout(sessionId).subscribe({
        next: () => {
          this.message = 'Logged out successfully!';
          this.isError = false;

          // Clear token and redirect after 1 second
          setTimeout(() => {
            this.authService.logout();
          }, 1000);
        },
        error: (error) => {
          this.isLoading = false;
          this.message = 'Logout failed: ' + (error.error?.message || error.message || 'Unknown error');
          this.isError = true;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    } catch (error) {
      this.message = 'Invalid token format';
      this.isError = true;
      this.isLoading = false;
    }
  }
}
