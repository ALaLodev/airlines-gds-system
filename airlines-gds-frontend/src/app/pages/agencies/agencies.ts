import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgencyService, Agency, AgencyMetrics } from '../../core/services/agency.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-agencies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agencies.html',
  styleUrls: ['./agencies.scss']
})
export class Agencies implements OnInit {
  private agencyService = inject(AgencyService);
  private cdr = inject(ChangeDetectorRef);

  // State
  agencies: Agency[] = [];
  metrics: AgencyMetrics | null = null;
  topPerformers: Agency[] = [];
  
  // Pagination & Filtering
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  searchQuery = '';
  statusFilter = '';
  regionFilter = '';
  
  // UI State
  isLoading = true;
  showAddModal = false;
  showRankingModal = false;
  successMessage = '';
  errorMessage = '';

  // Ranking Report
  allAgencies: Agency[] = [];
  rankingSortField: 'bookings30d' | 'complianceRate' | 'agencyName' = 'bookings30d';
  rankingSortDir: 'asc' | 'desc' = 'desc';

  // Forms
  newAgency: Partial<Agency> = {
    agencyName: '',
    iataCode: '',
    city: '',
    country: '',
    region: 'EMEA',
    status: 'ACTIVE',
    contactName: '',
    contactEmail: ''
  };

  private searchSubject = new Subject<string>();

  ngOnInit(): void {
    this.loadMetrics();
    this.loadTopPerformers();
    this.loadAgencies();
    this.loadAllAgenciesForRanking();

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.currentPage = 0;
      this.loadAgencies();
    });
  }

  loadAgencies(): void {
    this.isLoading = true;
    this.agencyService.getAgencies(this.currentPage, this.pageSize, this.searchQuery, this.statusFilter, this.regionFilter)
      .subscribe({
        next: (response) => {
          this.agencies = response.data;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Error loading agencies', error);
          this.errorMessage = 'Failed to load agencies. Please try again.';
          this.isLoading = false;
          this.cdr.markForCheck();
        }
      });
  }

  loadMetrics(): void {
    this.agencyService.getMetrics().subscribe({
      next: (metrics) => {
        this.metrics = metrics;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading metrics', err)
    });
  }

  loadTopPerformers(): void {
    this.agencyService.getTopPerformers().subscribe({
      next: (performers) => {
        this.topPerformers = performers;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading top performers', err)
    });
  }

  loadAllAgenciesForRanking(): void {
    this.agencyService.getAgencies(0, 1000, '', '', '').subscribe({
      next: (response) => {
        this.allAgencies = response.data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading all agencies for ranking', err)
    });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchQuery);
  }

  applyFiltersAndPagination(): void {
    this.loadAgencies();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadAgencies();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadAgencies();
    }
  }

  getInitials(name: string): string {
    if (!name) return '??';
    const words = name.split(' ');
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0].charAt(0) + words[words.length - 1].charAt(0)).toUpperCase();
  }

  getBookingPercentage(bookings: number): number {
    // Assuming max bookings for full bar is around 6000 (based on Azure Business Travel)
    const max = 6000;
    const percentage = (bookings / max) * 100;
    return Math.min(percentage, 100);
  }

  // Modal Actions
  openAddModal(): void {
    this.newAgency = {
      agencyName: '',
      iataCode: '',
      city: '',
      country: '',
      region: 'EMEA',
      status: 'ACTIVE',
      contactName: '',
      contactEmail: ''
    };
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  // Ranking Report methods
  openRankingModal(): void {
    this.showRankingModal = true;
  }

  closeRankingModal(): void {
    this.showRankingModal = false;
  }

  get rankedAgencies(): Agency[] {
    const sorted = [...this.allAgencies].sort((a, b) => {
      let valA: any = a[this.rankingSortField];
      let valB: any = b[this.rankingSortField];
      if (typeof valA === 'string') {
        valA = valA.toLowerCase();
        valB = (valB as string).toLowerCase();
      }
      if (this.rankingSortDir === 'asc') return valA > valB ? 1 : valA < valB ? -1 : 0;
      return valA < valB ? 1 : valA > valB ? -1 : 0;
    });
    return sorted;
  }

  get rankingMaxBookings(): number {
    if (this.allAgencies.length === 0) return 1;
    return Math.max(...this.allAgencies.map(a => a.bookings30d), 1);
  }

  get regionStats(): { region: string; count: number; totalBookings: number; avgCompliance: number }[] {
    const map = new Map<string, { count: number; totalBookings: number; totalCompliance: number }>();
    for (const a of this.allAgencies) {
      const r = a.region;
      const cur = map.get(r) || { count: 0, totalBookings: 0, totalCompliance: 0 };
      cur.count++;
      cur.totalBookings += a.bookings30d;
      cur.totalCompliance += a.complianceRate;
      map.set(r, cur);
    }
    return Array.from(map.entries()).map(([region, stats]) => ({
      region,
      count: stats.count,
      totalBookings: stats.totalBookings,
      avgCompliance: Math.round(stats.totalCompliance / stats.count)
    })).sort((a, b) => b.totalBookings - a.totalBookings);
  }

  get totalRankingBookings(): number {
    return this.allAgencies.reduce((sum, a) => sum + a.bookings30d, 0);
  }

  get avgRankingBookings(): number {
    if (this.allAgencies.length === 0) return 0;
    return Math.round(this.totalRankingBookings / this.allAgencies.length);
  }

  toggleRankingSort(field: 'bookings30d' | 'complianceRate' | 'agencyName'): void {
    if (this.rankingSortField === field) {
      this.rankingSortDir = this.rankingSortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.rankingSortField = field;
      this.rankingSortDir = field === 'agencyName' ? 'asc' : 'desc';
    }
  }

  getRankingSortIcon(field: string): string {
    if (this.rankingSortField !== field) return 'unfold_more';
    return this.rankingSortDir === 'asc' ? 'arrow_upward' : 'arrow_downward';
  }

  onSubmitAgency(): void {
    this.agencyService.createAgency(this.newAgency).subscribe({
      next: (agency) => {
        this.successMessage = 'Agency created successfully';
        this.closeAddModal();
        this.loadAgencies();
        this.loadMetrics();
        this.cdr.markForCheck();
        setTimeout(() => {
          this.successMessage = '';
          this.cdr.markForCheck();
        }, 3000);
      },
      error: (err) => {
        this.errorMessage = 'Failed to create agency';
        console.error(err);
        this.cdr.markForCheck();
      }
    });
  }
}
