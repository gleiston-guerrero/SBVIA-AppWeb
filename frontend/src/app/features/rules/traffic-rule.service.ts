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
export class TrafficRuleService {
  private readonly url = '/api/reglas-transito';
  constructor(private http: HttpClient) {}
  list(): Observable<TrafficRule[]> { return this.http.get<TrafficRule[]>(this.url); }
  create(regla: TrafficRule): Observable<TrafficRule> { return this.http.post<TrafficRule>(this.url, regla); }
  update(id: number, regla: TrafficRule): Observable<TrafficRule> { return this.http.put<TrafficRule>(`${this.url}/${id}`, regla); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}
