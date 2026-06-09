import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
// Asegúrate de que la ruta coincida con donde creaste tu booking.service.ts
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

  // Exponemos la librería Math para poder usar Math.min() en el HTML
  Math = Math;

  // Estado de los datos
  bookingList: BookingDetail[] = [];
  metrics?: BookingMetrics;
  
  // Estado de la paginación
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  isLoading = false;

  // Estado de los filtros bidireccionales
  filterPnr = '';
  filterStatus = 'All Statuses';
  filterAgency = 'All Agencies';

  ngOnInit(): void {
    this.loadRealData();
    this.loadMetrics();
  }

  loadRealData(): void {
    this.isLoading = true;
    const currentFilters = {
      pnr: this.filterPnr,
      status: this.filterStatus,
      agency: this.filterAgency
    };

    this.bookingService.getBookings(this.currentPage, this.pageSize, currentFilters).subscribe({
      next: (response: any) => {
        // Adaptamos al JSON de respuesta que me has pasado
        this.bookingList = response.data || []; 
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.isLoading = false;
      }
    });
  }

  loadMetrics(): void {
    this.bookingService.getBookingMetrics().subscribe({
      next: (data) => this.metrics = data,
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