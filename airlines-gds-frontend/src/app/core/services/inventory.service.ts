import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventoryItem {
  id: number;
  scheduleId: number;
  flightNumber: string;
  origin: string;
  destination: string;
  aircraftType: string;
  departureTime: string;
  economyTotal: number;
  economyBooked: number;
  premiumTotal: number;
  premiumBooked: number;
  businessTotal: number;
  businessBooked: number;
  firstTotal: number | null;
  firstBooked: number | null;
  status: 'ON_SALE' | 'LIMITED' | 'EARLY_BOOKING' | 'CLOSED';
  baseFare: number;
  availableSeats: number;
}

export interface InventoryMetrics {
  activeFlights: number;
  activeFlightsChange: number;
  avgLoadFactor: number;
  unsoldInventoryValue: number;
}

export interface InventoryAlert {
  type: 'CRITICAL' | 'WARNING' | 'INFO';
  title: string;
  message: string;
  icon: string;
}

export interface PaginatedInventoryResponse {
  data: InventoryItem[];
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/inventories';

  getInventories(page: number = 0, size: number = 5, search?: string, status?: string): Observable<PaginatedInventoryResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search) params = params.set('search', search);
    if (status) params = params.set('status', status);

    return this.http.get<PaginatedInventoryResponse>(this.apiUrl, { params });
  }

  getMetrics(): Observable<InventoryMetrics> {
    return this.http.get<InventoryMetrics>(`${this.apiUrl}/metrics`);
  }

  getAlerts(): Observable<InventoryAlert[]> {
    return this.http.get<InventoryAlert[]>(`${this.apiUrl}/alerts`);
  }

  updateInventory(id: number, data: any): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.apiUrl}/${id}`, data);
  }
}
