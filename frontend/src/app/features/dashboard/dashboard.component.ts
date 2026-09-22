import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { ScenarioService } from '../scenarios/scenario.service';
import { SimulationService } from '../practices/simulation.service';
import { UserService } from '../users/user.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslatePipe],
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
    private scenarioService: ScenarioService,
    private simulationService: SimulationService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.currentUser();
    this.loadMetrics();
  }

  private loadMetrics(): void {
    this.scenarioService.list(0, 1).subscribe({
      next: pagina => this.totalEscenarios = pagina.totalElements ?? pagina.content?.length ?? 0
    });
    
    if (this.user?.role === 'ADMINISTRADOR') {
      this.simulationService.getGlobalStatistics().subscribe({
        next: (stats: any) => {
          this.totalPracticas = stats.totalPracticas;
          this.promedio = stats.promedioGlobal;
          this.tasaAprobacion = stats.tasaAprobacionGlobal;
          this.cargandoMetricas = false;
        },
        error: () => this.cargandoMetricas = false
      });
      
      this.userService.list(0, 1).subscribe({
        next: pagina => this.totalUsuarios = pagina.totalElements ?? pagina.content?.length ?? 0
      });
    } else {
      this.simulationService.getMyPractices().subscribe({
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

  /** Returns the recommendation KEY; the text is translated in the template. */
  get recomendacionKey(): string {
    if (this.totalPracticas === 0) return 'dashboard.tipStart';
    if (this.promedio < 70) return 'dashboard.tipLow';
    if (this.promedio < 90) return 'dashboard.tipMid';
    return 'dashboard.tipHigh';
  }

  navigate(path: string): void {
    this.router.navigate([path]);
  }

  logout(): void {
    this.authService.logout();
  }
}
