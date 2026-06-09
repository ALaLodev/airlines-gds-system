import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

// 1. Definimos la interfaz de lo que nos devuelve tu backend de Spring Boot al hacer Login
export interface AuthResponse {
  token: string;
  username: string;
  roles?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  // URL de tu API Gateway. Apuntamos al endpoint de autenticación.
  private apiUrl = 'http://localhost:8080/api/auth'; 
  private readonly TOKEN_KEY = 'jwt_token';

  /**
   * Envía las credenciales al microservicio de autenticación a través del Gateway
   */
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(response => {
        // 2. Cuando el Backend responde con éxito, guardamos el JWT en el LocalStorage
        if (response && response.token) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          localStorage.setItem('email', response.username);
        }
      })
    );
  }

  /**
   * Borra la sesión y redirige al usuario al Login
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }

  /**
   * Recupera el token guardado para que los interceptores lo añadan a las cabeceras HTTP
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Verifica si el usuario tiene un token guardado (sesión activa)
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    // Aquí en el futuro podríamos añadir lógica para validar si el JWT ha expirado
    return token !== null;
  }
}