import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service'; 

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Usamos la función inject para pedirle el servicio a Angular
  const authService = inject(AuthService);
  
  // 2. Le pedimos el token de forma segura (él ya sabe qué clave usar)
  const token = authService.getToken();

  // 3. Si hay token, clonamos la petición y le añadimos la cabecera
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest);
  }

  // 4. Si no hay token, la mandamos tal cual
  return next(req);
};