import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardKpi, DashboardCharts, PaginatedResponse, AdminBooking } from '../models/admin.models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  // Apuntamos al Gateway de Spring Boot
  private readonly API_URL = 'http://localhost:8080/api/bookings/admin/dashboard';

  constructor(private http: HttpClient) {}

  getKpis(): Observable<DashboardKpi> {
    return this.http.get<DashboardKpi>(`${this.API_URL}/kpis`);
  }

  getCharts(): Observable<DashboardCharts> {
    return this.http.get<DashboardCharts>(`${this.API_URL}/charts`);
  }

  getRecentBookings(page: number = 0, size: number = 10): Observable<PaginatedResponse<AdminBooking>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<PaginatedResponse<AdminBooking>>(`${this.API_URL}/recent`, { params });
  }
}