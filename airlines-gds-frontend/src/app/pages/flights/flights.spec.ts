import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Flights } from './flights';
import { FlightService } from '../../core/services/flight.service';
import { of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('Flights', () => {
  let component: Flights;
  let fixture: ComponentFixture<Flights>;
  let mockFlightService: any;

  beforeEach(async () => {
    mockFlightService = {
      getFlights: () => of([
        {
          id: 1,
          flightNumber: 'AA123',
          origin: 'JFK',
          destination: 'LHR',
          departureTime: '2026-06-10T12:00:00',
          arrivalTime: '2026-06-10T18:00:00',
          price: 350,
          availableSeats: 120
        }
      ]),
      createFlight: () => of({
        id: 2,
        flightNumber: 'DL456',
        origin: 'ATL',
        destination: 'CDG',
        departureTime: '2026-06-10T14:00:00',
        arrivalTime: '2026-06-10T22:00:00',
        price: 450,
        availableSeats: 150
      })
    };

    await TestBed.configureTestingModule({
      imports: [Flights],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: FlightService, useValue: mockFlightService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Flights);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate metrics based on flights data', () => {
    expect(component.totalActiveRoutes).toBe(1);
    expect(component.onTimePerformance).toBe(100);
  });
});
