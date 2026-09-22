import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Simulation } from './simulation.model';

export interface DrivingMetrics {
  durationSeconds: number;
  velocidadPromedio: number;
  velocidadMaxima: number;
  excesosVelocidad: number;
  colisiones: number;
  salidasCarril: number;
  semaforosIgnorados: number;
  distanciaInsegura: number;
  semaforosRespetados: number;
}

export interface AiReport {
  resumen: string;
  aciertos: string[];
  errores: string[];
  nivelRiesgo: string;
  recomendaciones: string[];
  puntaje: number;
  mensajeMotivador: string;
  comparacion: string | null;
  origen: string;
}

export interface DrivingResult {
  simulation: Simulation;
  feedback: AiReport;
}

@Injectable({
  providedIn: 'root'
})
export class SimulationService {
  private apiUrl = '/api/simulations';

  constructor(private http: HttpClient) {}

  getMyPractices(): Observable<Simulation[]> {
    return this.http.get<Simulation[]>(`${this.apiUrl}/mis-practicas`);
  }

  getGlobalStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/estadisticas`);
  }

  getAll(): Observable<Simulation[]> {
    return this.http.get<Simulation[]>(this.apiUrl);
  }

  start(scenarioId: number): Observable<Simulation> {
    return this.http.post<Simulation>(`${this.apiUrl}/iniciar/${scenarioId}`, {});
  }

  finish(simulationId: number, finalScore: number): Observable<Simulation> {
    return this.http.post<Simulation>(`${this.apiUrl}/${simulationId}/finalizar`, { finalScore });
  }

  endDriving(simulationId: number, metricas: DrivingMetrics): Observable<DrivingResult> {
    return this.http.post<DrivingResult>(`${this.apiUrl}/${simulationId}/conduccion/finalizar`, metricas);
  }

  getFeedback(simulationId: number): Observable<AiReport> {
    return this.http.get<AiReport>(`${this.apiUrl}/${simulationId}/feedback`);
  }
}
