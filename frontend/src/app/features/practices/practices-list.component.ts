import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AiReport, SimulationService } from './simulation.service';
import { Simulation } from './simulation.model';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Scenario, ScenarioService } from '../scenarios/scenario.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-practices-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
  templateUrl: './practices-list.component.html',
  styleUrl: './practices-list.component.css'
})
export class PracticesListComponent implements OnInit {
  practicas: Simulation[] = [];
  loading = true;
  error = '';
  scenarios: Scenario[] = [];
  idEscenarioSeleccionado: number | null = null;
  cargandoEscenarios = true;
  filtro: 'todas' | 'finalizadas' | 'pendientes' = 'todas';
  practicaSeleccionada: Simulation | null = null;
  informeSeleccionado: AiReport | null = null;
  cargandoInforme = false;
  errorInforme = '';

  constructor(private simulationService: SimulationService, private scenarioService: ScenarioService) {}

  ngOnInit(): void {
    this.loadPractices();
    this.scenarioService.list(0, 100).subscribe({
      next: respuesta => {
        this.scenarios = respuesta.content;
        this.idEscenarioSeleccionado = this.scenarios[0]?.id ?? null;
        this.cargandoEscenarios = false;
      },
      error: () => this.cargandoEscenarios = false
    });
  }

  loadPractices(): void {
    this.simulationService.getMyPractices().subscribe({
      next: (data: any) => {
        this.practicas = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'No se pudieron load las prácticas.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  get finalizadas(): Simulation[] {
    return this.practicas.filter(practica => practica.completed || !!practica.endDate);
  }

  get promedio(): number {
    if (this.finalizadas.length === 0) return 0;
    const total = this.finalizadas.reduce((suma, practica) => suma + Number(practica.finalScore), 0);
    return Math.round((total / this.finalizadas.length) * 10) / 10;
  }

  get aprobadas(): number {
    return this.finalizadas.filter(practica => Number(practica.finalScore) >= 70).length;
  }

  get escenarioSeleccionado(): Scenario | undefined {
    return this.scenarios.find(scenario => scenario.id === this.idEscenarioSeleccionado);
  }

  get practicasFiltradas(): Simulation[] {
    if (this.filtro === 'finalizadas') return this.finalizadas;
    if (this.filtro === 'pendientes') return this.practicas.filter(practica => !practica.completed && !practica.endDate);
    return this.practicas;
  }

  get pendientes(): number {
    return this.practicas.length - this.finalizadas.length;
  }

  viewReport(practica: Simulation): void {
    if (!practica.completed && !practica.endDate) return;
    if (this.practicaSeleccionada?.simulationId === practica.simulationId) {
      this.closeReport();
      return;
    }
    this.practicaSeleccionada = practica;
    this.informeSeleccionado = null;
    this.errorInforme = '';
    this.cargandoInforme = true;
    this.simulationService.getFeedback(practica.simulationId).subscribe({
      next: (informe: any) => {
        this.informeSeleccionado = informe;
        this.cargandoInforme = false;
      },
      error: () => {
        this.errorInforme = 'No se pudo recuperar el informe de esta práctica.';
        this.cargandoInforme = false;
      }
    });
  }

  closeReport(): void {
    this.practicaSeleccionada = null;
    this.informeSeleccionado = null;
    this.errorInforme = '';
    this.cargandoInforme = false;
  }
}
