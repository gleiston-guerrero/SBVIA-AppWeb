import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BackupRecord } from './backup.model';

@Injectable({
  providedIn: 'root'
})
export class BackupService {
  private apiUrl = '/api/respaldos';

  constructor(private http: HttpClient) {}

  list(): Observable<BackupRecord[]> {
    return this.http.get<BackupRecord[]>(this.apiUrl);
  }

  generate(payload: any): Observable<BackupRecord> {
    return this.http.post<BackupRecord>(`${this.apiUrl}/generar`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  download(id: number): void {
    window.location.href = `${this.apiUrl}/descargar/${id}`;
  }
}
