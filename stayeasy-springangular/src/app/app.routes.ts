import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {authGuard} from './guards/auth-guard';
import {Homepage} from './pages/homepage/homepage';
import { AdminSessionsComponent } from './pages/admin-sessions/admin-sessions.component';
import {PropertyDetail} from './pages/property-detail/property-detail';
import { ReviewFormComponent } from './pages/review-form/review-form';

export const routes: Routes = [
  { path: 'login', component: Login},
  { path: 'homepage', component: Homepage,  canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'register', component: Register },
  { path: 'admin/sessions', component: AdminSessionsComponent },
  { path: 'property/:id', component: PropertyDetail },
  { path: 'properties/:id/review', component: ReviewFormComponent }
];
