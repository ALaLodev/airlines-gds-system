import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ServiceStatus {
  status: string;
  uptime: number;
  message: string;
}

export interface InfrastructureLoad {
  activeNodes: number;
  cpuUsage: number;
  memoryUsage: number;
  diskIo: number;
}

export interface SystemLog {
  timestamp: string;
  level: string;
  message: string;
}

export interface SystemHealthResponse {
  services: { [key: string]: ServiceStatus };
  globalLatencyMs: number;
  requestVolumeRpm: number;
  infrastructureLoad: InfrastructureLoad;
  logs: SystemLog[];
}

@Injectable({
  providedIn: 'root'
})
export class HealthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/health';

  getSystemHealth(): Observable<SystemHealthResponse> {
    return this.http.get<SystemHealthResponse>(this.apiUrl);
  }
}

