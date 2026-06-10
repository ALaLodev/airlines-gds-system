import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Flight {
  id?: number;
  flightNumber: string;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  availableSeats: number;
}

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/flights';

  /**
   * Retrieves all flights from the backend
   */
  getFlights(): Observable<Flight[]> {
    return this.http.get<Flight[]>(this.apiUrl);
  }

  /**
   * Creates/Schedules a new flight
   */
  createFlight(flight: Flight): Observable<Flight> {
    return this.http.post<Flight>(this.apiUrl, flight);
  }

  /**
   * Searches for flights by origin and destination
   */
  searchFlights(origin: string, destination: string): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.apiUrl}/search`, {
      params: { origin, destination }
    });
  }

  /**
   * Retrieves a single flight by ID
   */
  getFlightById(id: number): Observable<Flight> {
    return this.http.get<Flight>(`${this.apiUrl}/${id}`);
  }
}
