import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookingDetail } from '../../pages/bookings/bookings';

export interface PaginatedBookingsResponse {
  content: BookingDetail[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface BookingMetrics {
  totalVolume: string;
  completionRate: string;
  activeHolds: number;
  revenueImpact: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private http = inject(HttpClient);
  
  // URL unificada de tu API Gateway
  private apiUrl = 'http://localhost:8080/api/bookings';

  /**
   * Recupera las reservas desde el Backend aplicando paginación y filtros dinámicos
   */
  getBookings(page: number, size: number, filters: { pnr?: string, status?: string, agency?: string }): Observable<PaginatedBookingsResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    // Si el usuario ha escrito o seleccionado un filtro, lo añadimos a la petición
    if (filters.pnr) {
      params = params.set('pnr', filters.pnr.trim().toUpperCase());
    }
    if (filters.status && filters.status !== 'All Statuses') {
      params = params.set('status', filters.status.toUpperCase());
    }
    if (filters.agency && filters.agency !== 'All Agencies') {
      params = params.set('agency', filters.agency);
    }

    // El AuthInterceptor que hicimos añadirá el Token JWT automáticamente a esta llamada
    return this.http.get<PaginatedBookingsResponse>(`${this.apiUrl}/admin/dashboard/recent`, { params });
  }

  /**
   * Recupera los KPIs de negocio para las tarjetas analíticas inferiores
   * Alineado con el patrón API Composition de la Fase 9
   */
  getBookingMetrics(): Observable<BookingMetrics> {
    return this.http.get<BookingMetrics>(`${this.apiUrl}/admin/dashboard/kpis`);
  }
}