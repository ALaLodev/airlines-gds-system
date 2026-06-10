import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BookingService, BookingMetrics } from '../../core/services/booking.service';
import { AuthService } from '../../core/services/auth.service';
import { FlightService, Flight } from '../../core/services/flight.service';

export interface BookingDetail {
  pnr: string;
  userEmail: string;
  origin: string;
  destination: string;
  totalAmount: number;
  status: 'COMPLETED' | 'PENDING' | 'CANCELLED';
  bookingDate: string;
}

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule], // FormsModule es vital para los filtros
  templateUrl: './bookings.html',
  styleUrl: './bookings.scss'
})
export class Bookings implements OnInit {
  private bookingService = inject(BookingService);
  private authService = inject(AuthService);
  private flightService = inject(FlightService);
  private cdr = inject(ChangeDetectorRef);

  Math = Math;

  bookingList: BookingDetail[] = [];
  metrics?: BookingMetrics;

  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  isLoading = false;

  filterPnr = '';
  filterStatus = '';

  // Modal and creation form states
  showCreateModal = false;
  passengerEmail = '';
  selectedFlightId: number | null = null;
  availableFlights: Flight[] = [];
  successMessage = '';
  errorMessage = '';
  isSaving = false;

  private searchSubject = new Subject<string>();

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.currentPage = 0;
      this.loadRealData();
    });
    this.loadRealData();
    this.loadMetrics();
  }

  onSearchInput(): void {
    this.searchSubject.next(this.filterPnr);
  }

  loadRealData(): void {
    this.isLoading = true;
    const currentFilters: { pnr?: string; status?: string } = {};
    if (this.filterPnr.trim()) currentFilters.pnr = this.filterPnr;
    if (this.filterStatus) currentFilters.status = this.filterStatus;

    this.bookingService.getBookings(this.currentPage, this.pageSize, currentFilters).subscribe({
      next: (response) => {
        console.log('✅ loadRealData API success:', response);
        // Adaptamos al JSON de respuesta que me has pasado
        this.bookingList = response.data || []; 
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
        this.cdr.markForCheck();
        console.log('✅ loadRealData state updated: isLoading=', this.isLoading, ' list=', this.bookingList);
      },
      error: (err) => {
        console.error('❌ loadRealData Error:', err);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadMetrics(): void {
    this.bookingService.getBookingMetrics().subscribe({
      next: (data) => {
        this.metrics = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error cargando KPIs:', err)
    });
  }

  onFilterChange(): void {
    this.currentPage = 0; // Volvemos a la página 1 al filtrar
    this.loadRealData();
  }

  nextPage(): void {
    if ((this.currentPage + 1) * this.pageSize < this.totalElements) {
      this.currentPage++;
      this.loadRealData();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRealData();
    }
  }

  createNewBooking() {
    this.successMessage = '';
    this.errorMessage = '';
    this.passengerEmail = '';
    this.selectedFlightId = null;
    this.isSaving = false;
    
    // Load available flights
    this.flightService.getFlights().subscribe({
      next: (flightsList) => {
        this.availableFlights = flightsList || [];
        this.showCreateModal = true;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading flights for booking:', err);
        this.errorMessage = 'Failed to load flight schedules. Please ensure flight-service is running.';
        this.cdr.markForCheck();
      }
    });
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  getSelectedFlightPrice(): number {
    if (!this.selectedFlightId) return 0;
    const flight = this.availableFlights.find(f => f.id === Number(this.selectedFlightId));
    return flight ? flight.price : 0;
  }

  onSubmitBooking() {
    if (!this.passengerEmail.trim() || !this.selectedFlightId) {
      this.errorMessage = 'Please provide passenger email and select a flight.';
      return;
    }

    const price = this.getSelectedFlightPrice();
    if (price <= 0) {
      this.errorMessage = 'Selected flight price is invalid.';
      return;
    }

    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    // 1. Resolve passenger email to user ID
    this.authService.getUserIdByEmail(this.passengerEmail.trim()).subscribe({
      next: (userId) => {
        // 2. Create booking with resolved user ID
        const payload = {
          userId: userId,
          scheduleId: Number(this.selectedFlightId),
          totalAmount: price
        };

        this.bookingService.createBooking(payload).subscribe({
          next: (res) => {
            this.successMessage = `Booking created successfully! PNR: ${res.pnr}`;
            this.showCreateModal = false;
            this.loadRealData();
            this.loadMetrics();
            this.isSaving = false;
            this.cdr.markForCheck();
          },
          error: (err) => {
            console.error('Error creating booking:', err);
            this.errorMessage = 'Failed to create booking transaction on the backend.';
            this.isSaving = false;
            this.cdr.markForCheck();
          }
        });
      },
      error: (err) => {
        console.error('Error resolving email:', err);
        this.errorMessage = 'Passenger email is not registered in the system.';
        this.isSaving = false;
        this.cdr.markForCheck();
      }
    });
  }
}