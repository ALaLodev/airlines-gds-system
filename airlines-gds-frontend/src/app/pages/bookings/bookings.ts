import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BookingService, BookingMetrics } from '../../core/services/booking.service';

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
    console.log('Abriendo modal de creación de reservas...');
  }
}