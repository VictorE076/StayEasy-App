import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {TestLogoutComponent} from './pages/test-logout.component';
import {Register} from './pages/register/register';
import {authGuard} from './guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login},
  { path: 'test-logout', component: TestLogoutComponent,  canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'register', component: Register }
];
