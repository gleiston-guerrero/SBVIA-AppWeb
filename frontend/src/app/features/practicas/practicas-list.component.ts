import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InformeIA, SimulacionService } from './simulation.service';
import { Simulation } from './simulation.model';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Scenario, EscenarioService } from '../scenarios/scenario.service';

@Component({
  selector: 'app-practicas-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './practicas-list.component.html',
  styleUrl: './practicas-list.component.css'
})
export class PracticasListComponent implements OnInit {
  practicas: Simulation[] = [];
  loading = true;
  error = '';
  scenarios: Scenario[] = [];
  idEscenarioSeleccionado: number | null = null;
  cargandoEscenarios = true;
  filtro: 'todas' | 'finalizadas' | 'pendientes' = 'todas';
  practicaSeleccionada: Simulation | null = null;
  informeSeleccionado: InformeIA | null = null;
  cargandoInforme = false;
  errorInforme = '';

  constructor(private simulacionService: SimulacionService, private escenarioService: EscenarioService) {}

  ngOnInit(): void {
    this.cargarPracticas();
    this.escenarioService.listar(0, 100).subscribe({
      next: respuesta => {
        this.scenarios = respuesta.content;
        this.idEscenarioSeleccionado = this.scenarios[0]?.id ?? null;
        this.cargandoEscenarios = false;
      },
      error: () => this.cargandoEscenarios = false
    });
  }

  cargarPracticas(): void {
    this.simulacionService.getMisPracticas().subscribe({
      next: (data) => {
        this.practicas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'No se pudieron cargar las prácticas.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  get finalizadas(): Simulation[] {
    return this.practicas.filter(practica => practica.completada || !!practica.fechaFin);
  }

  get promedio(): number {
    if (this.finalizadas.length === 0) return 0;
    const total = this.finalizadas.reduce((suma, practica) => suma + Number(practica.puntajeFinal), 0);
    return Math.round((total / this.finalizadas.length) * 10) / 10;
  }

  get aprobadas(): number {
    return this.finalizadas.filter(practica => Number(practica.puntajeFinal) >= 70).length;
  }

  get escenarioSeleccionado(): Scenario | undefined {
    return this.scenarios.find(scenario => scenario.id === this.idEscenarioSeleccionado);
  }

  get practicasFiltradas(): Simulation[] {
    if (this.filtro === 'finalizadas') return this.finalizadas;
    if (this.filtro === 'pendientes') return this.practicas.filter(practica => !practica.completada && !practica.fechaFin);
    return this.practicas;
  }

  get pendientes(): number {
    return this.practicas.length - this.finalizadas.length;
  }

  verInforme(practica: Simulation): void {
    if (!practica.completada && !practica.fechaFin) return;
    if (this.practicaSeleccionada?.simulationId === practica.simulationId) {
      this.cerrarInforme();
      return;
    }
    this.practicaSeleccionada = practica;
    this.informeSeleccionado = null;
    this.errorInforme = '';
    this.cargandoInforme = true;
    this.simulacionService.getRetroalimentacion(practica.simulationId).subscribe({
      next: informe => {
        this.informeSeleccionado = informe;
        this.cargandoInforme = false;
      },
      error: () => {
        this.errorInforme = 'No se pudo recuperar el informe de esta práctica.';
        this.cargandoInforme = false;
      }
    });
  }

  cerrarInforme(): void {
    this.practicaSeleccionada = null;
    this.informeSeleccionado = null;
    this.errorInforme = '';
    this.cargandoInforme = false;
  }
}
