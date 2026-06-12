import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService, InventoryItem, InventoryMetrics, InventoryAlert } from '../../core/services/inventory.service';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class Inventory implements OnInit {
  private inventoryService = inject(InventoryService);
  private cdr = inject(ChangeDetectorRef);

  // Data
  inventories: InventoryItem[] = [];
  metrics: InventoryMetrics = {
    activeFlights: 0,
    activeFlightsChange: 0,
    avgLoadFactor: 0,
    unsoldInventoryValue: 0
  };
  alerts: InventoryAlert[] = [];

  // Filters
  searchQuery = '';
  selectedStatus = '';
  showFilters = true;

  // Pagination
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  totalPages = 0;
  isLastPage = false;

  // Loading
  isLoading = false;
  errorMessage = '';

  // Yield chart bars (simulated)
  yieldBars = [
    { height: 40, label: '00:00-02:00', isPeak: false },
    { height: 55, label: '02:00-04:00', isPeak: false },
    { height: 75, label: '04:00-06:00', isPeak: false },
    { height: 90, label: '06:00-08:00', isPeak: true },
    { height: 65, label: '08:00-10:00', isPeak: false },
    { height: 45, label: '10:00-12:00', isPeak: false },
    { height: 30, label: '12:00-14:00', isPeak: false },
    { height: 50, label: '14:00-16:00', isPeak: false },
    { height: 80, label: '16:00-18:00', isPeak: false },
    { height: 60, label: '18:00-20:00', isPeak: false },
    { height: 40, label: '20:00-22:00', isPeak: false },
    { height: 25, label: '22:00-00:00', isPeak: false }
  ];

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loadInventories();
    this.loadMetrics();
    this.loadAlerts();
  }

  loadInventories(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.inventoryService.getInventories(
      this.currentPage,
      this.pageSize,
      this.searchQuery || undefined,
      this.selectedStatus || undefined
    ).subscribe({
      next: (res) => {
        this.inventories = res.data;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        this.isLastPage = res.isLast;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading inventories:', err);
        this.errorMessage = 'Failed to load inventory data. Please make sure the backend services are running.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadMetrics(): void {
    this.inventoryService.getMetrics().subscribe({
      next: (data) => {
        this.metrics = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading metrics:', err)
    });
  }

  loadAlerts(): void {
    this.inventoryService.getAlerts().subscribe({
      next: (data) => {
        this.alerts = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading alerts:', err)
    });
  }

  onSearchChange(): void {
    this.currentPage = 0;
    this.loadInventories();
  }

  onStatusChange(): void {
    this.currentPage = 0;
    this.loadInventories();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadInventories();
    }
  }

  nextPage(): void {
    if (!this.isLastPage) {
      this.currentPage++;
      this.loadInventories();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadInventories();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 3;
    const start = Math.max(0, this.currentPage - 1);
    const end = Math.min(this.totalPages, start + maxVisible);
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }

  getPercentage(booked: number, total: number): number {
    if (total === 0) return 0;
    return Math.round((booked / total) * 100);
  }

  getFareColorClass(booked: number, total: number): string {
    const pct = this.getPercentage(booked, total);
    if (pct > 80) return 'fare-color--critical';
    if (pct > 50) return 'fare-color--filling';
    return 'fare-color--high';
  }

  getFareBarClass(booked: number, total: number): string {
    const pct = this.getPercentage(booked, total);
    if (pct > 80) return 'fare-bar--critical';
    if (pct > 50) return 'fare-bar--filling';
    return 'fare-bar--high';
  }

  formatStatus(status: string): string {
    switch (status) {
      case 'ON_SALE': return 'On Sale';
      case 'LIMITED': return 'Limited';
      case 'EARLY_BOOKING': return 'Early Booking';
      case 'CLOSED': return 'Closed';
      default: return status;
    }
  }

  formatTime(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
  }
}
