import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { Simulation } from '../practices/simulation.model';
import { SimulationService } from '../practices/simulation.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-supervision',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TranslatePipe],
  templateUrl: './supervision.component.html',
  styleUrls: ['./supervision.component.css', './supervision-admin-actions.component.css']
})
export class SupervisionComponent implements OnInit {
  practicas: Simulation[] = [];
  filtro = '';
  estado = '';
  isLoading = true;
  error = '';
  esAuditor = false;
  esAdmin = false;

  constructor(private auth: AuthService, private simulationService: SimulationService) {}

  ngOnInit(): void {
    const role = this.auth.currentUser()?.role;
    // El catálogo actual no tiene role AUDITOR: nadie recibe la vista de solo lectura.
    this.esAuditor = false;
    this.esAdmin = role === 'ADMINISTRADOR';
    this.simulationService.getAll().subscribe({
      next: (practicas: any) => { this.practicas = practicas; this.isLoading = false; },
      error: (error: any) => { this.error = error.error?.detail ?? 'No se pudo load la supervisión.'; this.isLoading = false; }
    });
  }

  get filtradas(): Simulation[] {
    const texto = this.filtro.trim().toLowerCase();
    return this.practicas.filter(practica => {
      const coincideTexto = !texto || [practica.username, practica.userEmail, practica.scenarioName]
        .some(value => value?.toLowerCase().includes(texto));
      return coincideTexto && this.matchesState(practica);
    });
  }

  private matchesState(practica: Simulation): boolean {
    if (!this.estado) return true;
    const finalizada = practica.completed || !!practica.endDate;
    if (this.estado === 'EN_PROGRESO') return !finalizada;
    if (!finalizada) return false;
    const aprobada = Number(practica.finalScore) >= 70;
    return (this.estado === 'APROBADA') === aprobada;
  }

  get finalizadas(): Simulation[] { return this.practicas.filter((p: any) => p.completed || !!p.endDate); }
  get promedio(): number {
    return this.finalizadas.length
      ? Math.round(this.finalizadas.reduce((total: any, p: any) => total + Number(p.finalScore), 0) / this.finalizadas.length)
      : 0;
  }
  get aprobadas(): number {
    return this.finalizadas.filter((p: any) => Number(p.finalScore) >= 70).length;
  }
  get conductores(): number { return new Set(this.practicas.map(p => p.userId).filter(Boolean)).size; }
}
