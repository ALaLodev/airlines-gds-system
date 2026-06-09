import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settings.html',
  styleUrl: './settings.scss'
})
export class Settings {
  
  // En el futuro, estos datos vendrán del backend (AuthService / UserService)
  userProfile = {
    fullName: 'Alexander Vance',
    email: 'a.vance@skylink-gds.aero',
    role: 'Global Administrator',
    is2FaEnabled: true
  };

  saveChanges() {
    console.log('Guardando configuración de usuario...', this.userProfile);
    // Aquí llamaríamos al servicio HTTP para hacer un PUT o PATCH
    alert('Configuración guardada correctamente.');
  }

}