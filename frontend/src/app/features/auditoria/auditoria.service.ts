import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface AuditLog {
  idAuditoria: number;
  nombreTabla: string;
  operacion: string;
  usuarioDb: string;
  usuarioApp: string;
  fechaHora: string;
  datosAnteriores: string;
  datosNuevos: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {
  private apiUrl = '/api/auditoria';

  constructor(private http: HttpClient) {}

  obtenerAuditoria(filtros: any): Observable<AuditLog[]> {
    let params = new HttpParams();
    if (filtros.tabla) params = params.set('tabla', filtros.tabla);
    if (filtros.operacion) params = params.set('operacion', filtros.operacion);
    if (filtros.user) params = params.set('user', filtros.user);
    if (filtros.fechaInicio) params = params.set('fechaInicio', filtros.fechaInicio);
    if (filtros.fechaFin) params = params.set('fechaFin', filtros.fechaFin);

    return this.http.get<AuditLog[]>(this.apiUrl, { params });
  }

  descargarReportePdf(filtros: any): Observable<Blob> {
    let params = new HttpParams();
    if (filtros.tabla) params = params.set('tabla', filtros.tabla);
    if (filtros.operacion) params = params.set('operacion', filtros.operacion);
    if (filtros.user) params = params.set('user', filtros.user);
    if (filtros.fechaInicio) params = params.set('fechaInicio', filtros.fechaInicio);
    if (filtros.fechaFin) params = params.set('fechaFin', filtros.fechaFin);

    return this.http.get(`${this.apiUrl}/reporte/pdf`, { 
      params, 
      responseType: 'blob' 
    });
  }
}
