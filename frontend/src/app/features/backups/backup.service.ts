import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Respaldo } from './backup.model';

@Injectable({
  providedIn: 'root'
})
export class BackupService {
  private apiUrl = '/api/backups';

  constructor(private http: HttpClient) {}

  list(): Observable<Respaldo[]> {
    return this.http.get<Respaldo[]>(this.apiUrl);
  }

  generate(payload: any): Observable<Respaldo> {
    return this.http.post<Respaldo>(`${this.apiUrl}/generate`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  download(id: number): void {
    window.location.href = `${this.apiUrl}/download/${id}`;
  }
}
