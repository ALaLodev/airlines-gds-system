import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService, Flight } from '../../core/services/flight.service';
import { FlightSeatMapModal } from './flight-seat-map-modal/flight-seat-map-modal';

@Component({
  selector: 'app-flights',
  standalone: true,
  imports: [CommonModule, FormsModule, FlightSeatMapModal],
  templateUrl: './flights.html',
  styleUrl: './flights.scss'
})
export class Flights implements OnInit {
  private flightService = inject(FlightService);
  private cdr = inject(ChangeDetectorRef);

  // Flight lists
  flights: Flight[] = [];
  filteredFlights: Flight[] = [];
  paginatedFlights: Flight[] = [];

  // Metrics
  totalActiveRoutes = 0;
  onTimePerformance = 94.2;
  flightsToday = 0;

  // Search & Filter
  searchQuery = '';
  selectedFilter: 'ALL' | 'IN_AIR' | 'DELAYED' = 'ALL';

  // Pagination
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;

  // Loading state
  isLoading = false;

  // Modal control
  showScheduleModal = false;

  // Modal form data
  newFlightNumber = '';
  newOrigin = '';
  newDestination = '';
  newDepartureTime = '';
  newArrivalTime = '';
  newPrice: number = 0;
  newAvailableSeats: number = 150;

  // Error/Success alerts
  successMessage = '';
  errorMessage = '';

  // Seat map modal
  selectedFlight: Flight | null = null;

  ngOnInit(): void {
    this.loadFlights();
  }

  loadFlights(): void {
    this.isLoading = true;
    this.flightService.getFlights().subscribe({
      next: (data) => {
        this.flights = data || [];
        this.applyFiltersAndPagination();
        this.calculateMetrics();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error fetching flights:', err);
        this.errorMessage = 'Failed to load flight schedules. Please make sure the backend services are running.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  calculateMetrics(): void {
    // Unique routes (origin-destination pairs)
    const routesSet = new Set(this.flights.map(f => `${f.origin}-${f.destination}`));
    this.totalActiveRoutes = routesSet.size;

    // Today's flights count (simplified check for today)
    const todayStr = new Date().toISOString().split('T')[0];
    this.flightsToday = this.flights.filter(f => f.departureTime.startsWith(todayStr)).length;

    // Default fallback values if lists are empty
    if (this.flights.length === 0) {
      this.totalActiveRoutes = 0;
      this.flightsToday = 0;
      this.onTimePerformance = 94.2;
    } else {
      // Calculate dynamic OTP (On-Time Performance) based on status mapper
      const onTimeCount = this.flights.filter(f => this.getFlightStatus(f) === 'On-Time').length;
      this.onTimePerformance = parseFloat(((onTimeCount / this.flights.length) * 100).toFixed(1));
    }
  }

  getFlightStatus(flight: Flight): 'On-Time' | 'Delayed' | 'Cancelled' {
    const code = flight.flightNumber || '';
    const hash = code.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    
    if (hash % 9 === 0) return 'Cancelled';
    if (hash % 6 === 0) return 'Delayed';
    return 'On-Time';
  }

  applyFiltersAndPagination(): void {
    // 1. Search Query filter (matches Flight Number, Origin, Destination)
    let temp = this.flights.filter(f => {
      const q = this.searchQuery.trim().toLowerCase();
      if (!q) return true;
      return (
        f.flightNumber.toLowerCase().includes(q) ||
        f.origin.toLowerCase().includes(q) ||
        f.destination.toLowerCase().includes(q)
      );
    });

    // 2. Status Category filter
    if (this.selectedFilter === 'DELAYED') {
      temp = temp.filter(f => this.getFlightStatus(f) === 'Delayed');
    } else if (this.selectedFilter === 'IN_AIR') {
      // For demonstration, "In Air" are flights whose departure is in the past and arrival is in the future
      const now = new Date();
      temp = temp.filter(f => {
        const dep = new Date(f.departureTime);
        const arr = new Date(f.arrivalTime);
        return dep <= now && arr >= now;
      });
    }

    this.filteredFlights = temp;
    this.totalElements = temp.length;
    this.totalPages = Math.ceil(this.totalElements / this.pageSize);

    // Keep current page within boundaries
    if (this.currentPage >= this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages - 1;
    } else if (this.totalPages === 0) {
      this.currentPage = 0;
    }

    // 3. Paginate
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedFlights = temp.slice(start, end);
  }

  onSearchChange(): void {
    this.currentPage = 0;
    this.applyFiltersAndPagination();
  }

  setFilter(filter: 'ALL' | 'IN_AIR' | 'DELAYED'): void {
    this.selectedFilter = filter;
    this.currentPage = 0;
    this.applyFiltersAndPagination();
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.applyFiltersAndPagination();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.applyFiltersAndPagination();
    }
  }

  openScheduleModal(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.newFlightNumber = '';
    this.newOrigin = '';
    this.newDestination = '';
    this.newDepartureTime = '';
    this.newArrivalTime = '';
    this.newPrice = 0;
    this.newAvailableSeats = 150;
    this.showScheduleModal = true;
  }

  closeScheduleModal(): void {
    this.showScheduleModal = false;
  }

  onSubmitSchedule(): void {
    if (
      !this.newFlightNumber.trim() ||
      !this.newOrigin.trim() ||
      !this.newDestination.trim() ||
      !this.newDepartureTime ||
      !this.newArrivalTime ||
      this.newPrice <= 0 ||
      this.newAvailableSeats <= 0
    ) {
      this.errorMessage = 'Please fill out all fields with valid values.';
      return;
    }

    const payload: Flight = {
      flightNumber: this.newFlightNumber.trim().toUpperCase(),
      origin: this.newOrigin.trim().toUpperCase(),
      destination: this.newDestination.trim().toUpperCase(),
      departureTime: this.newDepartureTime,
      arrivalTime: this.newArrivalTime,
      price: this.newPrice,
      availableSeats: this.newAvailableSeats
    };

    this.isLoading = true;
    this.flightService.createFlight(payload).subscribe({
      next: (savedFlight) => {
        this.successMessage = `Flight ${savedFlight.flightNumber} scheduled successfully!`;
        this.errorMessage = '';
        this.showScheduleModal = false;
        this.loadFlights(); // Reload list
      },
      error: (err) => {
        console.error('Error creating flight:', err);
        this.errorMessage = 'Failed to schedule the flight. Please verify the input values (e.g., flight number must be unique).';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  getCapacityPercentage(flight: Flight): number {
    // Assuming a standard max capacity of 262 seats for Boeing 787-9
    const total = 262;
    const booked = total - flight.availableSeats;
    const pct = Math.round((booked / total) * 100);
    return Math.max(0, Math.min(pct, 100));
  }

  openSeatMap(flight: Flight): void {
    this.selectedFlight = flight;
  }

  closeSeatMap(): void {
    this.selectedFlight = null;
    // Refresh flight list to reflect any new bookings
    this.loadFlights();
  }
}
