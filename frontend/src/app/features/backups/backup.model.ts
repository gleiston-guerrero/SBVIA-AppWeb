export interface Respaldo {
  idRespaldo: number;
  nombreArchivo: string;
  tipo: string;
  estado: string;
  startDate: string;
  endDate?: string;
  tamanioBytes?: number;
  detalles?: string;
  modalidad?: string;
  fechaProgramada?: string;
  comentario?: string;
}
