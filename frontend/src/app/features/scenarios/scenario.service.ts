import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Scenario {
  id?: number;
  name: string;
  description: string;
  lengthKm?: number;
  estimatedTimeMinutes?: number;
  roadType: string;
  difficultyLevel: string;
  weatherType: string;
  trafficDensity: string;
  activo?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class EscenarioService {
  private readonly API_URL = '/api/scenarios';

  constructor(private http: HttpClient) { }

  list(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'scenarioId,desc');
    return this.http.get(this.API_URL, { params });
  }

  findById(id: number): Observable<Scenario> {
    return this.http.get<Scenario>(`${this.API_URL}/${id}`);
  }

  create(scenario: Scenario): Observable<Scenario> {
    return this.http.post<Scenario>(this.API_URL, scenario);
  }

  update(id: number, scenario: Scenario): Observable<Scenario> {
    return this.http.put<Scenario>(`${this.API_URL}/${id}`, scenario);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
