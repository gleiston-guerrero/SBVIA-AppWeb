import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Simulation } from './simulation.model';

export interface MetricasConduccion {
  duracionSegundos: number;
  velocidadPromedio: number;
  velocidadMaxima: number;
  excesosVelocidad: number;
  colisiones: number;
  salidasCarril: number;
  semaforosIgnorados: number;
  distanciaInsegura: number;
  semaforosRespetados: number;
}

export interface InformeIA {
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

export interface ResultadoConduccion {
  simulation: Simulation;
  feedback: InformeIA;
}

@Injectable({
  providedIn: 'root'
})
export class SimulacionService {
  private apiUrl = '/api/simulations';

  constructor(private http: HttpClient) {}

  getMisPracticas(): Observable<Simulation[]> {
    return this.http.get<Simulation[]>(`${this.apiUrl}/mis-practicas`);
  }

  getEstadisticasGlobales(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/estadisticas`);
  }

  getTodas(): Observable<Simulation[]> {
    return this.http.get<Simulation[]>(this.apiUrl);
  }

  iniciar(scenarioId: number): Observable<Simulation> {
    return this.http.post<Simulation>(`${this.apiUrl}/iniciar/${scenarioId}`, {});
  }

  finalizar(simulationId: number, puntajeFinal: number): Observable<Simulation> {
    return this.http.post<Simulation>(`${this.apiUrl}/${simulationId}/finalizar`, { puntajeFinal });
  }

  finalizarConduccion(simulationId: number, metricas: MetricasConduccion): Observable<ResultadoConduccion> {
    return this.http.post<ResultadoConduccion>(`${this.apiUrl}/${simulationId}/conduccion/finalizar`, metricas);
  }

  getRetroalimentacion(simulationId: number): Observable<InformeIA> {
    return this.http.get<InformeIA>(`${this.apiUrl}/${simulationId}/feedback`);
  }
}
