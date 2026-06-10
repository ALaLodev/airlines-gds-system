import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Agency {
  id: number;
  agencyName: string;
  iataCode: string;
  city: string;
  country: string;
  region: 'EMEA' | 'APAC' | 'AMER';
  contactName: string;
  contactEmail: string;
  status: 'ACTIVE' | 'SUSPENDED';
  bookings30d: number;
  complianceRate: number;
}

export interface AgencyMetrics {
  totalAgencies: number;
  totalAgenciesChange: number;
  activeBookings: number;
  activeBookingsChange: number;
  revenueMtd: number;
  revenueMtdChange: number;
  complianceRate: number;
}

export interface PaginatedAgenciesResponse {
  data: Agency[];
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AgencyService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/agencies';

  getAgencies(page: number = 0, size: number = 10, search?: string, status?: string, region?: string): Observable<PaginatedAgenciesResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    if (search) params = params.set('search', search);
    if (status) params = params.set('status', status);
    if (region) params = params.set('region', region);

    return this.http.get<PaginatedAgenciesResponse>(this.apiUrl, { params });
  }

  getAgencyById(id: number): Observable<Agency> {
    return this.http.get<Agency>(`${this.apiUrl}/${id}`);
  }

  createAgency(agency: Partial<Agency>): Observable<Agency> {
    return this.http.post<Agency>(this.apiUrl, agency);
  }

  updateAgency(id: number, agency: Partial<Agency>): Observable<Agency> {
    return this.http.put<Agency>(`${this.apiUrl}/${id}`, agency);
  }

  deleteAgency(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getMetrics(): Observable<AgencyMetrics> {
    return this.http.get<AgencyMetrics>(`${this.apiUrl}/metrics`);
  }

  getTopPerformers(): Observable<Agency[]> {
    return this.http.get<Agency[]>(`${this.apiUrl}/top-performers`);
  }
}
