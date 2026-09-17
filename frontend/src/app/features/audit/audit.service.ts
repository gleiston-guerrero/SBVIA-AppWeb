import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface AuditLog {
  idAuditoria: number;
  tableName: string;
  operation: string;
  dbUser: string;
  appUser: string;
  timestamp: string;
  previousData: string;
  newData: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuditService {
  private apiUrl = '/api/auditoria';

  constructor(private http: HttpClient) {}

  getAuditLogs(filtros: any): Observable<AuditLog[]> {
    let params = new HttpParams();
    if (filtros.tabla) params = params.set('tabla', filtros.tabla);
    if (filtros.operation) params = params.set('operation', filtros.operation);
    if (filtros.user) params = params.set('user', filtros.user);
    if (filtros.startDate) params = params.set('startDate', filtros.startDate);
    if (filtros.endDate) params = params.set('endDate', filtros.endDate);

    return this.http.get<AuditLog[]>(this.apiUrl, { params });
  }

  downloadPdfReport(filtros: any): Observable<Blob> {
    let params = new HttpParams();
    if (filtros.tabla) params = params.set('tabla', filtros.tabla);
    if (filtros.operation) params = params.set('operation', filtros.operation);
    if (filtros.user) params = params.set('user', filtros.user);
    if (filtros.startDate) params = params.set('startDate', filtros.startDate);
    if (filtros.endDate) params = params.set('endDate', filtros.endDate);

    return this.http.get(`${this.apiUrl}/reporte/pdf`, { 
      params, 
      responseType: 'blob' 
    });
  }
}
