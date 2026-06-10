import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../core/services/admin';
import { DashboardKpi, PaginatedResponse, AdminBooking, DashboardCharts } from '../../core/models/admin.models';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  kpis: DashboardKpi | null = null;
  recentBookings: PaginatedResponse<AdminBooking> | null = null;
  chartsData: DashboardCharts | null = null;
  statusLegend: { label: string; color: string; percentage: number }[] = [];
  
  salesChart: any;
  statusChart: any;

  ngOnInit(): void {
    // Pedimos KPIs 
    this.adminService.getKpis().subscribe({
      next: (datos) => {
        this.kpis = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error KPIs:', err)
    });

    // Pedimos Tabla
    this.adminService.getRecentBookings(0, 5).subscribe({
      next: (datos) => {
        this.recentBookings = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error Tabla:', err)
    });

    // Pedimos Gráficas 
    this.adminService.getCharts().subscribe({
      next: (datos) => {
        this.chartsData = datos;
        
        // Obligamos a Angular a procesar el @if(chartsData) e insertar los <canvas>
        this.cdr.detectChanges();

        // Mantenemos un micro-retraso de 10ms SOLO para darle tiempo al 
        // navegador a "construir" el lienzo en la pantalla antes de pintarlo.
        setTimeout(() => {
          this.renderSalesChart(datos.salesPerDay);
          this.renderStatusChart(datos.statusDistribution);
        }, 10);
      },
      error: (err) => console.error('Error Gráficas:', err)
    });
  }

  // =========================================================================
  // LÓGICA DEL DESPLEGABLE (LAST 7 / 30 DAYS)
  // =========================================================================
  
  // 1. Variable que controla cuántos días pintar (por defecto 7)
  selectedDays: number = 7;

  // 2. Método que se dispara al cambiar el <select> del HTML
  onTimeframeChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedDays = parseInt(selectElement.value, 10);
    
    // Si ya tenemos los datos descargados, redibujamos la gráfica con el nuevo rango
    if (this.chartsData) {
      this.renderSalesChart(this.chartsData.salesPerDay);
    }
  }

  // =========================================================================
  // GRÁFICO DE BARRAS DINÁMICO
  // =========================================================================
  
  renderSalesChart(salesData: { [key: string]: number }) {
    const canvas = document.getElementById('salesChart') as HTMLCanvasElement;
    if (!canvas) {
      console.warn('El lienzo salesChart no está listo aún.');
      return;
    }

    if (this.salesChart) {
      this.salesChart.destroy();
    }

    const labels: string[] = [];
    const data: number[] = [];
    
    const daysName = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    const monthsName = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
    
    const today = new Date();
    
    // Bucle dinámico: Usa this.selectedDays (7 o 30) en lugar del "6" estático
    for (let i = this.selectedDays - 1; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(d.getDate() - i);
      
      // Lógica visual: Si son 7 días mostramos "MON", si son 30 mostramos fecha "12 OCT"
      if (this.selectedDays === 7) {
        labels.push(daysName[d.getDay()]);
      } else {
        labels.push(`${d.getDate()} ${monthsName[d.getMonth()]}`);
      }
      
      // Buscamos en los datos que vienen del backend
      const dateString = d.toLocaleDateString('en-CA');
      data.push(salesData[dateString] || 0);
    }

    // 2. Destacar el día con más ventas en color oscuro
    const maxVal = Math.max(...data);
    const backgroundColors = data.map(val => 
      (val === maxVal && val > 0) ? '#4f57aa' : 'rgba(79, 87, 170, 0.2)'
    );

    // 3. Pintar la gráfica
    this.salesChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Volume',
          data: data,
          backgroundColor: backgroundColors,
          borderRadius: 4,
          borderSkipped: false,
          // Si hay 30 días, hacemos las barras un poco más anchas para que se vea bien
          barPercentage: this.selectedDays === 30 ? 0.8 : 0.4, 
          categoryPercentage: 0.8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { 
          legend: { display: false },
          tooltip: {
            backgroundColor: '#322d56',
            padding: 10,
            cornerRadius: 4,
          }
        },
        scales: {
          x: {
            grid: { display: false }, 
            border: { display: false }, 
            ticks: {
              // Si son 30 días, ocultamos algunos labels para que no se apelotonen los textos
              autoSkip: this.selectedDays === 30,
              maxTicksLimit: 10,
              font: { family: 'Inter', size: 10, weight: 'bold' },
              color: '#5f5a86' 
            }
          },
          y: {
            display: false, // Oculta el eje Y por completo
            beginAtZero: true
          }
        }
      }
    });
  }

  renderStatusChart(statusData: { [key: string]: number }) {
    const canvas = document.getElementById('statusChart') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.statusChart) {
      this.statusChart.destroy();
    }

    const labels = Object.keys(statusData);
    const data = Object.values(statusData);

    // 1. Mapeamos los colores exactos de tu Dashboard-Design.md
    const colorMap: { [key: string]: string } = {
      'COMPLETED': '#2a3385', // on-primary-container (Azul oscuro)
      'PENDING': '#dff685',   // tertiary-container (Lima suave)
      'CANCELLED': '#ac3149', // error (Rojo)
      'FAILED': '#770326'     // error-dim (Rojo oscuro)
    };

    // 2. Calculamos los porcentajes para nuestra leyenda HTML
    const total = data.reduce((sum, val) => sum + val, 0);
    this.statusLegend = labels.map((label, index) => {
      const value = data[index];
      return {
        label: label,
        color: colorMap[label] || '#9e9ab5', // Color por defecto si viene un estado raro
        percentage: total > 0 ? Math.round((value / total) * 100) : 0
      };
    });

    // 3. Pintamos el gráfico pero OCULTAMOS su leyenda por defecto
    this.statusChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: labels.map(l => colorMap[l] || '#9e9ab5'),
          borderWidth: 0 // Sin bordes entre las rebanadas, como en tu diseño
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '80%', // Hacemos el donut más fino para que quepa el texto
        plugins: { 
          legend: { display: false }, // Apagamos la leyenda fea de Chart.js
          tooltip: { enabled: true }
        }
      }
    });
  }
}
