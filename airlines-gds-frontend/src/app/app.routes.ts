import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard'; 
import { Login } from './pages/login/login';
import { Settings } from './pages/settings/settings';
import { Bookings } from './pages/bookings/bookings';
import { Flights } from './pages/flights/flights';
import { Agencies } from './pages/agencies/agencies';
import { Inventory } from './pages/inventory/inventory';
import { Health } from './pages/health/health';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'settings', component: Settings, canActivate: [authGuard] },
  { path: 'bookings', component: Bookings, canActivate: [authGuard] },
  { path: 'flights', component: Flights, canActivate: [authGuard] },
  { path: 'agencies', component: Agencies, canActivate: [authGuard] },
  { path: 'inventory', component: Inventory, canActivate: [authGuard] },
  { path: 'health', component: Health, canActivate: [authGuard] }
];