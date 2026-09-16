import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService, AuditLog } from './audit.service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.css']
})
export class AuditComponent implements OnInit {
  registros: AuditLog[] = [];
  cargando = false;
  error = '';

  filtros = {
    tabla: '',
    operation: '',
    user: '',
    startDate: '',
    endDate: ''
  };

  modalAbierto = false;
  registroSeleccionado: AuditLog | null = null;
  datosAntiguosObj: any = null;
  datosNuevosObj: any = null;

  constructor(private auditoriaService: AuditService) {}

  ngOnInit(): void {
    this.cargarAuditoria();
  }

  cargarAuditoria(): void {
    this.cargando = true;
    this.error = '';
    
    // Preparar fechas si están seleccionadas (añadir horas)
    const params = { ...this.filtros };
    if (params.startDate) params.startDate += 'T00:00:00';
    if (params.endDate) params.endDate += 'T23:59:59';

    this.auditoriaService.obtenerAuditoria(params).subscribe({
      next: (data: any) => {
        this.registros = data;
        this.cargando = false;
      },
      error: (err: any) => {
        this.error = 'No se pudo cargar el historial de auditoría.';
        this.cargando = false;
        console.error(err);
      }
    });
  }

  descargarReporte(): void {
    const params = { ...this.filtros };
    if (params.startDate) params.startDate += 'T00:00:00';
    if (params.endDate) params.endDate += 'T23:59:59';

    this.auditoriaService.descargarReportePdf(params).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte_auditoria_${new Date().getTime()}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (err: any) => {
        this.error = 'Error al generar el PDF.';
        console.error(err);
      }
    });
  }

  verDetalle(registro: AuditLog): void {
    this.registroSeleccionado = registro;
    try {
      this.datosAntiguosObj = registro.previousData ? JSON.parse(registro.previousData) : null;
    } catch { this.datosAntiguosObj = registro.previousData; }
    
    try {
      this.datosNuevosObj = registro.newData ? JSON.parse(registro.newData) : null;
    } catch { this.datosNuevosObj = registro.newData; }

    this.modalAbierto = true;
  }

  cerrarModal(): void {
    this.modalAbierto = false;
    this.registroSeleccionado = null;
  }
}
