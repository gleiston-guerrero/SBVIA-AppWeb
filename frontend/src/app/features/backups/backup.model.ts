export interface Respaldo {
  id: number;
  fileName: string;
  type: string;
  status: string;
  startDate: string;
  endDate?: string;
  sizeBytes?: number;
  details?: string;
  mode?: string;
  scheduledDate?: string;
  comment?: string;
}
