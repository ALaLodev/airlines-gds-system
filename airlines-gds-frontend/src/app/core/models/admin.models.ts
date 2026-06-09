// src/app/core/models/admin.models.ts

export interface DashboardKpi {
  totalRevenue: number;
  totalBookings: number;
  successfulBookings: number;
  successRate: number;
}

export interface DashboardCharts {
  statusDistribution: { [key: string]: number };
  salesPerDay: { [key: string]: number };
}

export interface AdminBooking {
  pnr: string;
  userEmail: string;
  origin: string;
  destination: string;
  totalAmount: number;
  status: string;
  bookingDate: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}