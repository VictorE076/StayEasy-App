import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {authGuard} from './guards/auth-guard';
import {Homepage} from './pages/homepage/homepage';
import { AdminSessionsComponent } from './pages/admin-sessions/admin-sessions.component';

export const routes: Routes = [
  { path: 'login', component: Login},
  { path: 'homepage', component: Homepage,  canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'register', component: Register },
  { path: 'admin/sessions', component: AdminSessionsComponent }
];
