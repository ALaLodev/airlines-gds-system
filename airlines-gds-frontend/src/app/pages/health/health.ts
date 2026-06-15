import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HealthService, SystemHealthResponse } from '../../core/services/health.service';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './health.html',
  styleUrls: ['./health.scss']
})
export class Health implements OnInit, OnDestroy {
  private healthService = inject(HealthService);
  private cdr = inject(ChangeDetectorRef);

  healthData: SystemHealthResponse | null = null;
  loading = true;
  error = '';
  private refreshSubscription?: Subscription;

  ngOnInit(): void {
    this.fetchHealthData();
    // Auto-refresh every 5 seconds
    this.refreshSubscription = interval(5000).subscribe(() => {
      this.fetchHealthData();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  fetchHealthData(): void {
    this.healthService.getSystemHealth().subscribe({
      next: (data) => {
        this.healthData = data;
        this.loading = false;
        this.error = '';
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to fetch system health', err);
        this.error = 'Failed to load system health data. Please make sure the backend services are running.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  manualRefresh(): void {
    this.loading = true;
    this.cdr.markForCheck();
    this.fetchHealthData();
  }

  getServiceStatus(serviceName: string) {
    if (!this.healthData || !this.healthData.services) return null;
    return this.healthData.services[serviceName];
  }

  exportReport(): void {
    alert('Export Status Report feature is coming soon.');
  }
}

