import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="under-construction">
      <h1>Seat Inventory Management</h1>
      <p>This module is under construction.</p>
    </div>
  `,
  styles: [`
    .under-construction {
      padding: 32px;
      text-align: center;
      color: #322d56;
      font-family: 'Inter', sans-serif;
    }
  `]
})
export class Inventory {}
