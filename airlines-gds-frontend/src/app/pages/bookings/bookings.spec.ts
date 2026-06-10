import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Bookings } from './bookings';
import { BookingService } from '../../core/services/booking.service';
import { AuthService } from '../../core/services/auth.service';
import { FlightService } from '../../core/services/flight.service';
import { of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('Bookings', () => {
  let component: Bookings;
  let fixture: ComponentFixture<Bookings>;
  let mockBookingService: any;
  let mockAuthService: any;
  let mockFlightService: any;

  beforeEach(async () => {
    mockBookingService = {
      getBookings: () => of({ data: [], currentPage: 0, pageSize: 5, totalElements: 0, totalPages: 0, isLast: true }),
      getBookingMetrics: () => of({ totalRevenue: 0, totalBookings: 0, successfulBookings: 0, successRate: 0 }),
      createBooking: () => of({ pnr: 'ABC123' })
    };

    mockAuthService = {
      getUserIdByEmail: () => of(1)
    };

    mockFlightService = {
      getFlights: () => of([])
    };

    await TestBed.configureTestingModule({
      imports: [Bookings],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: BookingService, useValue: mockBookingService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: FlightService, useValue: mockFlightService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Bookings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
