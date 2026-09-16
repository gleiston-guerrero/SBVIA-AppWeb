import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { EscenarioService } from '../scenarios/scenario.service';
import { SimulationService } from '../practices/simulation.service';
import { UsuarioService } from '../users/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  user: any;
  totalEscenarios = 0;
  totalUsuarios = 0;
  totalPracticas = 0;
  promedio = 0;
  tasaAprobacion = 0;
  cargandoMetricas = true;

  constructor(
    private authService: AuthService,
    private router: Router,
    private escenarioService: EscenarioService,
    private simulationService: SimulationService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.currentUser();
    this.cargarMetricas();
  }

  private cargarMetricas(): void {
    this.escenarioService.listar(0, 1).subscribe({
      next: pagina => this.totalEscenarios = pagina.totalElements ?? pagina.content?.length ?? 0
    });
    
    if (this.user?.role === 'ADMINISTRADOR') {
      this.simulationService.getEstadisticasGlobales().subscribe({
        next: (stats: any) => {
          this.totalPracticas = stats.totalPracticas;
          this.promedio = stats.promedioGlobal;
          this.tasaAprobacion = stats.tasaAprobacionGlobal;
          this.cargandoMetricas = false;
        },
        error: () => this.cargandoMetricas = false
      });
      
      this.usuarioService.listar(0, 1).subscribe({
        next: pagina => this.totalUsuarios = pagina.totalElements ?? pagina.content?.length ?? 0
      });
    } else {
      this.simulationService.getMisPracticas().subscribe({
        next: (practicas: any) => {
          this.totalPracticas = practicas.length;
          const finalizadas = practicas.filter((p: any) => p.completed || p.endDate);
          this.promedio = finalizadas.length
            ? Math.round(finalizadas.reduce((total: any, p: any) => total + Number(p.finalScore), 0) / finalizadas.length)
            : 0;
          this.tasaAprobacion = finalizadas.length
            ? Math.round(finalizadas.filter((p: any) => Number(p.finalScore) >= 70).length * 100 / finalizadas.length)
            : 0;
          this.cargandoMetricas = false;
        },
        error: () => this.cargandoMetricas = false
      });
    }
  }

  get recomendacion(): string {
    if (this.totalPracticas === 0) return 'Empieza con un scenario de dificultad baja para establecer tu primera referencia.';
    if (this.promedio < 70) return 'Repite los scenarios practicados y concéntrate en reducir las infractions de mayor penalización.';
    if (this.promedio < 90) return 'Vas por buen camino. Prueba scenarios de mayor dificultad para fortalecer tu anticipación.';
    return 'Tu rendimiento es sobresaliente. Mantén la constancia con scenarios y condiciones variadas.';
  }

  navigate(path: string): void {
    this.router.navigate([path]);
  }

  logout(): void {
    this.authService.logout();
  }
}
