import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Location, CommonModule } from '@angular/common';
import { Sidebar } from './layout/sidebar/sidebar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private location = inject(Location);

  // Getter síncrono que lee la barra de direcciones directamente.
  // Es imposible que Angular se desincronice con esto.
  get isFullscreenRoute(): boolean {
    const path = this.location.path();
    // Si estás en localhost:4200/ (vacío) o en /login, devuelve true.
    return path === '' || path.includes('/login');
  }
}