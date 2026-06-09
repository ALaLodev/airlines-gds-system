import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard'; 
import { Login } from './pages/login/login';
import { Settings } from './pages/settings/settings';
import { Bookings } from './pages/bookings/bookings';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard },
  { path: 'login', component: Login },
  { path: 'settings', component: Settings},
  { path: 'bookings', component: Bookings}
];