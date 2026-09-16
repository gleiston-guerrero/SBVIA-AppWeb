import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TrafficRule {
  id?: number;
  codigo: string;
  nombre: string;
  description?: string;
  categoria: string;
  penalizacionBase: number;
  activa?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ReglaTransitoService {
  private readonly url = '/api/reglas-transito';
  constructor(private http: HttpClient) {}
  listar(): Observable<TrafficRule[]> { return this.http.get<TrafficRule[]>(this.url); }
  crear(regla: TrafficRule): Observable<TrafficRule> { return this.http.post<TrafficRule>(this.url, regla); }
  actualizar(id: number, regla: TrafficRule): Observable<TrafficRule> { return this.http.put<TrafficRule>(`${this.url}/${id}`, regla); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}
