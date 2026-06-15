import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, inject, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Flight } from '../../../core/services/flight.service';
import { BookingService, BookedSeat, SeatReservationRequest, SeatReservationResponse } from '../../../core/services/booking.service';

// ── Seat domain model ──────────────────────────────────────────────────────────

export type CabinClass = 'FIRST' | 'BUSINESS' | 'PREMIUM_ECONOMY' | 'ECONOMY';
export type SeatStatus = 'available' | 'occupied' | 'selected';

export interface Seat {
  id: string;        // e.g. "14A"
  row: number;
  col: string;       // e.g. "A"
  status: SeatStatus;
  cabin: CabinClass;
  hasWindow: boolean;
  hasExtraLegroom: boolean;
}

export interface CabinSection {
  name: CabinClass;
  label: string;
  rows: Seat[][];    // each inner array is one row of seats
  layout: string;    // e.g. "2-2" | "2-4-2" | "3-3-3"
  isExitAfter?: boolean;
}

// ── Aircraft Layout Definition (Boeing 787-9 style) ───────────────────────────

const AIRCRAFT_CONFIG: { cabin: CabinClass; label: string; startRow: number; endRow: number; cols: string[]; groups: string[][]; exitAfter: boolean }[] = [
  {
    cabin: 'FIRST',
    label: 'First Class',
    startRow: 1,
    endRow: 3,
    cols: ['A', 'B', 'E', 'F'],
    groups: [['A', 'B'], ['E', 'F']],
    exitAfter: true
  },
  {
    cabin: 'BUSINESS',
    label: 'Business Class',
    startRow: 4,
    endRow: 7,
    cols: ['A', 'B', 'D', 'E'],
    groups: [['A', 'B'], ['D', 'E']],
    exitAfter: true
  },
  {
    cabin: 'PREMIUM_ECONOMY',
    label: 'Premium Economy',
    startRow: 8,
    endRow: 13,
    cols: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J'],
    groups: [['A', 'B', 'C'], ['D', 'E', 'F'], ['G', 'H', 'J']],
    exitAfter: true
  },
  {
    cabin: 'ECONOMY',
    label: 'Economy Class',
    startRow: 14,
    endRow: 33,
    cols: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J'],
    groups: [['A', 'B', 'C'], ['D', 'E', 'F'], ['G', 'H', 'J']],
    exitAfter: false
  }
];

const WINDOW_COLS = new Set(['A', 'J', 'F']); // approximate window seats
const EXIT_ROW_NUMBERS = new Set([8, 14]);

/** Deterministic RNG based on flight ID + seat id for realistic-looking distribution */
function pseudoRandom(seed: number): number {
  const x = Math.sin(seed) * 10000;
  return x - Math.floor(x);
}

@Component({
  selector: 'app-flight-seat-map-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './flight-seat-map-modal.html',
  styleUrl: './flight-seat-map-modal.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class FlightSeatMapModal implements OnInit, OnDestroy {
  @Input({ required: true }) flight!: Flight;
  @Output() close = new EventEmitter<void>();

  private bookingService = inject(BookingService);
  private cdr = inject(ChangeDetectorRef);

  // ── Seat map state ────────────────────────────────────────────────────────
  cabins: CabinSection[] = [];
  selectedSeat: Seat | null = null;
  isLoadingSeats = true;
  isReserving = false;

  // ── Reservation result state ─────────────────────────────────────────────
  reservationResult: SeatReservationResponse | null = null;
  reservationError = '';
  showConfirmation = false;

  // ── Inventory stats ───────────────────────────────────────────────────────
  get totalSeats(): number {
    return this.cabins.reduce((sum, c) => sum + c.rows.reduce((rs, r) => rs + r.length, 0), 0);
  }
  get occupiedSeats(): number {
    return this.cabins.reduce((sum, c) => sum + c.rows.reduce((rs, r) => rs + r.filter(s => s.status === 'occupied').length, 0), 0);
  }
  get occupancyPct(): number {
    return this.totalSeats > 0 ? Math.round((this.occupiedSeats / this.totalSeats) * 100) : 0;
  }

  // ── Lifecycle ─────────────────────────────────────────────────────────────
  ngOnInit(): void {
    this.buildAircraftLayout();
    this.loadBookedSeats();
    document.body.style.overflow = 'hidden';
  }

  ngOnDestroy(): void {
    document.body.style.overflow = '';
  }

  // ── Build initial seat map (all available) ────────────────────────────────
  private buildAircraftLayout(): void {
    this.cabins = AIRCRAFT_CONFIG.map(cfg => {
      const rows: Seat[][] = [];
      let seatIndex = 0;
      for (let row = cfg.startRow; row <= cfg.endRow; row++) {
        const rowSeats: Seat[] = cfg.cols.map(col => {
          seatIndex++;
          const seed = (this.flight.id ?? 1) * 1000 + row * 20 + col.charCodeAt(0);
          return {
            id: `${row}${col}`,
            row,
            col,
            status: 'available',
            cabin: cfg.cabin,
            hasWindow: WINDOW_COLS.has(col),
            hasExtraLegroom: EXIT_ROW_NUMBERS.has(row)
          };
        });
        rows.push(rowSeats);
      }
      return {
        name: cfg.cabin,
        label: cfg.label,
        rows,
        layout: cfg.groups.map(g => g.length).join('-'),
        isExitAfter: cfg.exitAfter
      };
    });
  }

  // ── Load booked seats from API ────────────────────────────────────────────
  private loadBookedSeats(): void {
    if (!this.flight.id) {
      this.markSeatsFromOccupancy();
      this.isLoadingSeats = false;
      this.cdr.markForCheck();
      return;
    }

    this.bookingService.getBookedSeats(this.flight.id).subscribe({
      next: (bookedSeats: BookedSeat[]) => {
        const occupiedSet = new Set(bookedSeats.map(s => s.seatNumber));
        this.cabins.forEach(cabin => {
          cabin.rows.forEach(row => {
            row.forEach(seat => {
              if (occupiedSet.has(seat.id)) {
                seat.status = 'occupied';
              }
            });
          });
        });
        this.isLoadingSeats = false;
        this.cdr.markForCheck();
      },
      error: () => {
        // Fallback: use availableSeats count for approximate distribution
        this.markSeatsFromOccupancy();
        this.isLoadingSeats = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** Fallback when API is unavailable: distribute occupied seats deterministically */
  private markSeatsFromOccupancy(): void {
    const totalSeats = this.totalSeats;
    const occupied = Math.max(0, totalSeats - (this.flight.availableSeats ?? 0));
    let marked = 0;
    const seed = this.flight.id ?? 42;

    this.cabins.forEach(cabin => {
      cabin.rows.forEach(row => {
        row.forEach(seat => {
          if (marked < occupied) {
            const r = pseudoRandom(seed + seat.id.charCodeAt(0) + seat.row * 7);
            if (r < 0.7) {
              seat.status = 'occupied';
              marked++;
            }
          }
        });
      });
    });
  }

  // ── Seat selection ────────────────────────────────────────────────────────
  selectSeat(seat: Seat): void {
    if (seat.status === 'occupied') return;

    // Deselect previous
    if (this.selectedSeat) {
      const prev = this.findSeatById(this.selectedSeat.id);
      if (prev && prev.status === 'selected') {
        prev.status = 'available';
      }
    }

    // Select new
    seat.status = 'selected';
    this.selectedSeat = seat;
    this.reservationResult = null;
    this.reservationError = '';
  }

  private findSeatById(id: string): Seat | null {
    for (const cabin of this.cabins) {
      for (const row of cabin.rows) {
        const seat = row.find(s => s.id === id);
        if (seat) return seat;
      }
    }
    return null;
  }

  // ── Reserve seat ──────────────────────────────────────────────────────────
  reserveSeat(): void {
    if (!this.selectedSeat || this.isReserving) return;

    this.isReserving = true;
    this.reservationError = '';

    // Use a fixed admin userId (1) as the panel is an admin-only tool
    const userId = 1;

    const priceMap: Record<CabinClass, number> = {
      'FIRST': (this.flight.price ?? 0) * 5,
      'BUSINESS': (this.flight.price ?? 0) * 3,
      'PREMIUM_ECONOMY': (this.flight.price ?? 0) * 1.5,
      'ECONOMY': (this.flight.price ?? 0)
    };

    const payload: SeatReservationRequest = {
      userId,
      scheduleId: this.flight.id!,
      totalAmount: parseFloat(priceMap[this.selectedSeat.cabin].toFixed(2)),
      seatNumber: this.selectedSeat.id,
      cabinClass: this.selectedSeat.cabin
    };

    this.bookingService.reserveSeat(payload).subscribe({
      next: (result: SeatReservationResponse) => {
        // Mark seat as occupied in the map
        const seat = this.findSeatById(this.selectedSeat!.id);
        if (seat) seat.status = 'occupied';

        this.reservationResult = result;
        this.showConfirmation = true;
        this.selectedSeat = null;
        this.isReserving = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.reservationError = err?.error?.message ?? 'Reservation failed. Please try again.';
        this.isReserving = false;
        this.cdr.markForCheck();
      }
    });
  }

  // ── Seat groups (for template rendering by aisle) ────────────────────────
  getSeatGroups(cabin: CabinSection, row: Seat[]): Seat[][] {
    const cfg = AIRCRAFT_CONFIG.find(c => c.cabin === cabin.name)!;
    let offset = 0;
    return cfg.groups.map(group => {
      const seats = row.slice(offset, offset + group.length);
      offset += group.length;
      return seats;
    });
  }

  // ── Utility getters ───────────────────────────────────────────────────────
  getCabinPrice(cabin: CabinClass): number {
    const price = this.flight.price ?? 0;
    const multipliers: Record<CabinClass, number> = {
      'FIRST': 5, 'BUSINESS': 3, 'PREMIUM_ECONOMY': 1.5, 'ECONOMY': 1
    };
    return Math.round(price * multipliers[cabin]);
  }

  getCabinLabel(cabin: CabinClass | string): string {
    const labels: Record<string, string> = {
      'FIRST': 'First Class', 'BUSINESS': 'Business', 'PREMIUM_ECONOMY': 'Premium Economy', 'ECONOMY': 'Economy'
    };
    return labels[cabin] ?? cabin;
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('seatmap-overlay')) {
      this.onClose();
    }
  }

  onClose(): void {
    this.close.emit();
  }

  dismissConfirmation(): void {
    this.showConfirmation = false;
    this.reservationResult = null;
  }
}
