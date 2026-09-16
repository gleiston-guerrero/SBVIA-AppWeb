import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Simulation } from './simulation.model';
import { SimulationService } from './simulation.service';

interface Infraction {
  nombre: string;
  penalizacion: number;
  cantidad: number;
}

@Component({
  selector: 'app-simulation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './simulation.component.html',
  styleUrl: './simulation.component.css'
})
export class SimulationComponent implements OnInit, OnDestroy {
  practica?: Simulation;
  resultado?: Simulation;
  segundos = 0;
  cargando = true;
  finalizando = false;
  error = '';
  private temporizador?: ReturnType<typeof setInterval>;

  infractions: Infraction[] = [
    { nombre: 'Exceso de velocidad', penalizacion: 15, cantidad: 0 },
    { nombre: 'No respetar una señal', penalizacion: 20, cantidad: 0 },
    { nombre: 'Cambio de carril inseguro', penalizacion: 10, cantidad: 0 },
    { nombre: 'Frenado brusco', penalizacion: 5, cantidad: 0 }
  ];

  constructor(private route: ActivatedRoute, private simulationService: SimulationService) {}

  ngOnInit(): void {
    const scenarioId = Number(this.route.snapshot.paramMap.get('scenarioId'));
    if (!Number.isInteger(scenarioId) || scenarioId <= 0) {
      this.error = 'El scenario seleccionado no es válido.';
      this.cargando = false;
      return;
    }
    this.simulationService.iniciar(scenarioId).subscribe({
      next: (practica: any) => {
        this.practica = practica;
        this.cargando = false;
        this.temporizador = setInterval(() => this.segundos++, 1000);
      },
      error: (err: any) => {
        this.error = err.error?.detail ?? 'No se pudo iniciar la práctica.';
        this.cargando = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.temporizador) clearInterval(this.temporizador);
  }

  registrar(infraction: Infraction): void {
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

  finalizar(): void {
    if (!this.practica || this.finalizando) return;
    this.finalizando = true;
    this.simulationService.finalizar(this.practica.simulationId, this.puntaje).subscribe({
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
