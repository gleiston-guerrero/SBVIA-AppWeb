import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Simulation } from './simulation.model';
import { SimulationService } from './simulation.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

interface Infraction {
  nombre: string;
  penalizacion: number;
  cantidad: number;
}

@Component({
  selector: 'app-simulation',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslatePipe],
  templateUrl: './simulation.component.html',
  styleUrl: './simulation.component.css'
})
export class SimulationComponent implements OnInit, OnDestroy {
  practica?: Simulation;
  resultado?: Simulation;
  segundos = 0;
  isLoading = true;
  finalizando = false;
  error = '';
  private temporizador?: ReturnType<typeof setInterval>;

  infractions: Infraction[] = [
    { nombre: 'sim.infSpeeding', penalizacion: 15, cantidad: 0 },
    { nombre: 'sim.infSignal', penalizacion: 20, cantidad: 0 },
    { nombre: 'sim.infLane', penalizacion: 10, cantidad: 0 },
    { nombre: 'sim.infBraking', penalizacion: 5, cantidad: 0 }
  ];

  constructor(private route: ActivatedRoute, private simulationService: SimulationService) {}

  ngOnInit(): void {
    const scenarioId = Number(this.route.snapshot.paramMap.get('scenarioId'));
    if (!Number.isInteger(scenarioId) || scenarioId <= 0) {
      this.error = 'El escenario seleccionado no es válido.';
      this.isLoading = false;
      return;
    }
    this.simulationService.start(scenarioId).subscribe({
      next: (practica: any) => {
        this.practica = practica;
        this.isLoading = false;
        this.temporizador = setInterval(() => this.segundos++, 1000);
      },
      error: (err: any) => {
        this.error = err.error?.detail ?? 'No se pudo iniciar la práctica.';
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.temporizador) clearInterval(this.temporizador);
  }

  recordInfraction(infraction: Infraction): void {
    infraction.cantidad++;
  }

  get puntaje(): number {
    const descuento = this.infractions.reduce(
      (total, infraction) => total + infraction.penalizacion * infraction.cantidad, 0);
    return Math.max(0, 100 - descuento);
  }

  get tiempo(): string {
    const minutos = Math.floor(this.segundos / 60).toString().padStart(2, '0');
    const segundos = (this.segundos % 60).toString().padStart(2, '0');
    return `${minutos}:${segundos}`;
  }

  finish(): void {
    if (!this.practica || this.finalizando) return;
    this.finalizando = true;
    this.simulationService.finish(this.practica.simulationId, this.puntaje).subscribe({
      next: resultado => {
        this.resultado = resultado;
        this.finalizando = false;
        if (this.temporizador) clearInterval(this.temporizador);
      },
      error: (err: any) => {
        this.error = err.error?.detail ?? 'No se pudo finalizar la práctica.';
        this.finalizando = false;
      }
    });
  }
}
